package com.jarveis.frame.task;

import com.jarveis.frame.bean.ReflectionUtils;
import com.jarveis.frame.dbs.DbsCache;
import com.jarveis.frame.dbs.DbsConst;
import com.jarveis.frame.dbs.DbsUtils;
import com.jarveis.frame.dbs.ServiceCode;
import com.jarveis.frame.task.bean.TaskConfig;
import com.jarveis.frame.util.CharacterUtil;
import com.jarveis.frame.util.HttpUtil;
import com.jarveis.frame.util.Param;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 订阅服务端的任务
 * <pre>
 * TaskSubscribe用于serviceNode从serviceServer上获取服务的任务。
 *
 * TaskSubscribe的启动取决于dbs_name_server是否配置。
 *
 * 工作原理：
 * TaskSubscribe从PublishServer获取任务后，通过解析器添加对应任务
 *
 * 如何刷新风控配置或清除无效的风控配置，serviceServer在返回风控配置的同时，会返回当前配置的更新时间；
 * 本地如果有相同配置会更新风控配置时间，如果本地中没有更新配置时间的，则说明配置已失效，则会清除。
 * </pre>
 *
 * @author zll
 * @since 2020-12-08
 */
public class TaskSubscribe {

    private static final Logger log = LoggerFactory.getLogger(TaskSubscribe.class);

    private String[] servers;
    private long time = 20000;

    /**
     * 初始化任务
     */
    public void init() {
        // 服务器节点
        String serviceServer = DbsCache.getConst(DbsConst.DBS_SERVICE_SERVER);
        if (StringUtils.isEmpty(serviceServer)) {
            return;
        }
        // 注册服务器
        servers = StringUtils.split(serviceServer, CharacterUtil.SEPARATOR);
    }

    /**
     * 执行任务
     */
    public void execute() {
        // 同步时请求参数对象
        Param in, out;
        // 同步时返回消息
        String message = null;
        Map<String, String> params = new HashMap<String, String>(1);
        try {
            String dbsLocal = DbsCache.getConst(DbsConst.DBS_LOCAL);
            in = new Param(Param.REQ);
            in.getHead().setProperty(Param.LABEL_FUNCID, ServiceCode.TASK_PUBLISH_SERVICE);
            in.getHead().setProperty(Param.LABEL_DATATYPE, Param.DT_JSON);

            TaskConfig taskConfig = TaskCache.getTaskConfig();
            taskConfig.setRemain(TaskPool.getInstance(TaskDaemon.getInstance()).getRemainSize());
            in.getBody().setPropertys(taskConfig.getProperties());

            // 汇报任务执行状态
            for (String code : TaskCache.getTaskStateMap().keySet()) {
                if (TaskCache.getTaskStateMap().get(code) == 2) {
                    Param info = in.getBody().addParam("taskstate");
                    info.setPropertys(new Param(TaskCache.getTaskInfoMap().get(code).toString()).getBody().getPropertys());
                    TaskCache.getTaskStateMap().put(code, 0);
                }
            }

            // 遍历当前集群列表
            for (int i = 0; i < servers.length; i++) {
                if (servers[i].equals(dbsLocal)) {
                    continue;
                }
                params.put("_message", in.toXmlString());
                String remoteNode = DbsUtils.getDbsURI(servers[i]);
                message = HttpUtil.doPost(remoteNode, params);

                if (StringUtils.isNotEmpty(message)) {
                    out = new Param(message);
                    creatTask(out);
                } else {
                    log.warn("获取远程服务器任务失败（" + remoteNode + "）");
                }
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    /**
     * @param out
     */
    private void creatServerTask(Param out) {
        TaskDaemon taskDaemon = TaskDaemon.getInstance();
        List<Param> list = out.getBody().getChilds("taskInfo");
        long timestamp = out.getHead().getLong("@timestamp");
        // 时间戳大于当前时间并且是服务端
        if (TaskCache.compareTimestamp(timestamp)) {
            // 响应无任务，删除本地任务保存同步
            if (list.isEmpty()) {
                TaskCache.cleanTasks();
                taskDaemon.cleanTasks();
            }
            Map<String, ITask> templist = new HashMap<>();
            // 服务端同步任务
            for (Param p : list) {
                String code = p.getString("@code");
                String param = p.getString("@param");
                String clazz = p.getString("@clazz");
                String status = p.getString("@status");
                ITask.Status status1 = "Sleep".equals(status) ? ITask.Status.Sleep : "Init".equals(status) ? ITask.Status.Init : "Start".equals(status) ? ITask.Status.Start : ITask.Status.Stop;
                String sleepTime = p.getString("@sleepTime");
                String[] nodes = StringUtils.split(p.getString("@nodes"), "|");
                String[] nodetypekey = StringUtils.split(p.getString("@nodetypekey"), "|");
                String[] nodetypeval = StringUtils.split(p.getString("@nodetypeval"), "|");
                String[] nodelastkey = StringUtils.split(p.getString("@nodelastkey"), "|");
                String[] nodelastval = StringUtils.split(p.getString("@nodelastval"), "|");
                Map<String, Integer> nodetype = new HashMap<>();
                for (int i = 0; i < nodetypekey.length; i++) {
                    nodetype.put(nodetypekey[i], (Integer.valueOf(nodetypeval[i])));
                }

                Map<String, Long> nodeLast = new HashMap<>();
                for (int i = 0; i < nodelastkey.length; i++) {
                    nodeLast.put(nodelastkey[i], Long.valueOf(nodelastval[i]));
                    if (!StringUtils.isEmpty(nodelastval[i]) && Long.valueOf(nodelastval[i]) != 0) {
                        TaskCache.putTasknodeMap(code, nodelastkey[i], Long.valueOf(nodelastval[i]));
                    }
                }
                ITask task = taskDaemon.getTask(code);
                TaskInfo taskInfo = null;
                if (task == null) {
                    // 不存在任务 需要添加
                    taskInfo = new TaskInfo(code, sleepTime, param);
                    taskInfo.setStatus(deSleep(sleepTime) + taskDaemon.getNowTime(), status1);
                    taskInfo.setNodes(nodes);
                    taskInfo.setNodeType(nodetype);
                    taskInfo.setClazz(clazz);
                    task = new ITask() {
                        @Override
                        public void execute() {

                        }
                    }.setTaskInfo(taskInfo);
                } else {
                    // 存在相同任务，修改属性
                    taskInfo = task.getTaskInfo();
//                    taskInfo.setStatus(taskInfo.getRunTime(),status1);
                    taskInfo.setSleepTime(sleepTime);
                    taskInfo.setNodes(nodes);
                    taskInfo.setClazz(clazz);
                    taskInfo.setParam(param);
                    taskInfo.setNodeType(nodetype);
                }
                // 覆盖任务缓存
                TaskCache.putTaskInfoMap(taskInfo);
                taskDaemon.addTask(task);
                templist.put(code, task);
            }
            List<ITask> iTaskList = new ArrayList<>(taskDaemon.getTask());
            for (ITask task : iTaskList) {
                String code = task.getTaskInfo().getCode();
                if (!templist.containsKey(code)) {
                    // 删除任务
                    taskDaemon.delTask(code);
                    TaskCache.removeTaskInfo(code);
                }
            }
            // 持久化数据，更新文件
            if (taskDaemon.getisStart() && taskDaemon.getisWait()) {
                taskDaemon.start();
            }
            new TaskFormat().format();
        }
    }

    private void creatTask(Param out) {
        List<Param> list = out.getBody().getChilds("taskInfo");
        // 服务端同步任务
        for (Param p : list) {
            try {
                String code = p.getString("@code");
                String param = p.getString("@param");
                String clazz = p.getString("@clazz");
                String sleepTime = p.getString("@sleepTime");
                String[] nodes = StringUtils.split(p.getString("@nodes"), "|");
                String[] nodetypekey = StringUtils.split(p.getString("@nodetypekey"), "|");
                String[] nodetypeval = StringUtils.split(p.getString("@nodetypeval"), "|");
                TaskInfo taskInfo = TaskCache.getTaskInfo(code);
                if (TaskCache.getTaskInfoMap().containsKey(code) && taskInfo.getSysTime() + time > new Date().getTime()) {
                    // 20秒内执行过防重
                    continue;
                }
                taskInfo = new TaskInfo(code, sleepTime, param);
                taskInfo.setStatus(0, ITask.Status.Init);
                taskInfo.setNodes(nodes);
                Map<String, Integer> nodetype = new HashMap<>();
                for (int i = 0; i < nodetypekey.length; i++) {
                    nodetype.put(nodetypekey[i], (Integer.valueOf(nodetypeval[i])));
                }
                taskInfo.setNodeType(nodetype);
                if (StringUtils.isEmpty(clazz)) {
                    continue;
                }
                taskInfo.setClazz(clazz);
                ITask task = null;
                Object object = ReflectionUtils.newInstance(clazz);
                if (object instanceof ITask) {
                    task = (ITask) object;
                    task.setTaskInfo(taskInfo);
                } else {
                    continue;
                }
                task.setSysTime(0l);
                // 任务不存在时执行 放重
                taskInfo.initNodeType();
                taskInfo.setSysTime(new Date().getTime());
                TaskCache.putTaskInfoMap(taskInfo);
                TaskCache.putTaskStateMap(code, 0);
                // node执行任务
                TaskPool.getInstance(TaskDaemon.getInstance()).runTask(task);
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
            }
        }
    }

    private long deSleep(String sleepTime) {
        long runTime = 0;
        int len = sleepTime.length();
        if (len > 1) {
            char c = sleepTime.charAt(len - 1);
            int n = Integer.parseInt(sleepTime.substring(0, len - 1), 10);

            if (c == 's') {
                runTime = runTime + n * 1000;
            } else if (c == 'm') {
                runTime = runTime + n * 60000;
            } else if (c == 'h') {
                runTime = runTime + n * 3600000;
            } else if (c == 'd') {
                runTime = runTime + n * 86400000;
            } else {
                runTime = Long.MAX_VALUE;
            }
        } else {
            runTime = Long.MAX_VALUE;
        }
        return runTime;
    }
}

