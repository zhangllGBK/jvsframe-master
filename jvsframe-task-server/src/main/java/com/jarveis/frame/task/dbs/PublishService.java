package com.jarveis.frame.task.dbs;

import com.jarveis.frame.dbs.Service;
import com.jarveis.frame.dbs.ServiceCode;
import com.jarveis.frame.dbs.ant.Function;
import com.jarveis.frame.task.TaskCache;
import com.jarveis.frame.task.TaskInfo;
import com.jarveis.frame.task.bean.TaskConfig;
import com.jarveis.frame.util.Param;
import com.jarveis.frame.util.ParamException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 发布任务
 * <pre>
 * 1.node端向server端进行数据同步
 *   1.1.node上报本地的任务池的情况
 *   1.2.node上报本地已执行的任务情况
 *   1.3.server端回复需要执行的任务信息
 * 2.server-A端向server-B端进行数据同步
 *   2.1.server-A向server-B同步，任务调度信息
 *   2.2.server-B向server-A回复，任务调度信息
 * </pre>
 *
 * @author liuguojun
 * @see com.jarveis.frame.task.threads.TaskSubscribe
 * @since 2020-06-17
 */
@Function(code = ServiceCode.TASK_PUBLISH_SERVICE)
public class PublishService implements Service {

    private static final Logger log = LoggerFactory.getLogger(PublishService.class);

    @Override
    public Param callService(Param in) {
        Param out = null;

        try {
            if (log.isDebugEnabled()) {
                log.debug(in.toXmlString());
            }
            out = new Param(Param.RESP);
            // 封装请求参数
            String nodedbs = in.getHead().getString("@nodedbs"); // 请求端服务器类型
            String dbsmachine = in.getBody().getString("@dbsmachine"); // node编号
            int remain = in.getBody().getInteger("@remain"); // node 闲置任务线程数

            out.getHead().setProperty("@timestamp", TaskCache.getTimestamp());
            if ("serviceServer".equals(nodedbs)) {
                processServerReq(in, out);
            } else {
                processNodeReq(in, out);
                if (remain == 0) {
                    // 无可用任务线程
                    return out;
                }
            }

            for (TaskInfo taskInfo : TaskCache.getTaskInfoMap().values()) {
                if (taskInfo.containsNode(dbsmachine)) {
                    TaskInfo task = new TaskInfo(taskInfo);
                    Param taskParam;
                    if (StringUtils.isEmpty(task.getParam())) {
                        taskParam = new Param(Param.REQ);
                    } else {
                        taskParam = new Param(task.getParam());
                    }

                    Param childinfo = taskParam.getBody().addParam("taskinfo");
                    String[] nodes = task.getNodes();
                    int percent = 100 / nodes.length;
                    int i = Arrays.asList(nodes).indexOf(dbsmachine);
                    childinfo.setProperty("@beginpercent", i * percent);
                    if (i + 1 == nodes.length) {
                        childinfo.setProperty("@endpercent", 100);
                    } else {
                        childinfo.setProperty("@endpercent", (i + 1) * percent);
                    }
                    task.setParam(taskParam.toXmlString());
                    // 当前node请求 在任务存在对应
                    // 当前任务所有所有分配的node状态 value 0待发布，1已发布，2已执行
                    Map<String, Integer> map = task.getNodeType();
                    Integer type = map.get(dbsmachine);
                    if (type != null && type == 0) {
                        // 任务待发布
                        Param info = out.getBody().addParam("taskInfo");
                        info.setPropertys(new Param(task.toString()).getBody().getPropertys());
                        map.put(dbsmachine, 1);
                        taskInfo.setNodeType(map);
                        TaskCache.compareTimestamp(TaskCache.getTimestamp() + 100);
                    }
                }
            }

            if (log.isDebugEnabled()) {
                log.debug(out.toXmlString());
            }

        } catch (Exception ex) {
            if (out != null) {
                out.getHead().setProperty(Param.LABEL_ERROR, Param.ERROR_EXCEPTION);
            }
            log.error(ex.getMessage(), ex);
        }

        return out;
    }

    private void processServerReq(Param in, Param out) throws ParamException {
        for (TaskInfo taskInfo : TaskCache.getTaskInfoMap().values()) {
            Param info = out.getBody().addParam("taskInfo");
            info.setPropertys(new Param(taskInfo.toString()).getBody().getPropertys());
        }
        List<Param> list = in.getBody().getChilds("node");
        for (Param p : list) {
            String dbsMachine = p.getString("@dbsmachine"); // 机器编号
            String dbsLocal = p.getString("@dbslocal"); // 服务地址
            String taskType = p.getString("@tasktype"); // 支持任务类型
            int remain = p.getInteger("@remain"); // 闲置任务线程数
            int minSize = p.getInteger("@minsize"); // 保持最小线程数
            int maxSize = p.getInteger("@maxsize"); // 最大任务线程数
            TaskConfig taskConfig = new TaskConfig(dbsMachine, dbsLocal, remain, taskType, minSize, maxSize);
            taskConfig.compareTimestamp(new Date().getTime());
            taskConfig.setType("remote");
            // 添加到node列表
            TaskCache.addTaskConfig(taskConfig);
        }
    }

    private void processNodeReq(Param in, Param out) {
        String dbsMachine = in.getBody().getString("@dbsmachine"); // 机器编号
        String dbsLocal = in.getBody().getString("@dbslocal"); // 服务地址
        String taskType = in.getBody().getString("@tasktype"); // 支持任务类型
        int remain = in.getBody().getInteger("@remain"); // 闲置任务线程数
        int minSize = in.getBody().getInteger("@minsize"); // 保持最小线程数
        int maxSize = in.getBody().getInteger("@maxsize"); // 最大任务线程数
        // 更新node服务器信息
        TaskConfig taskConfig = new TaskConfig(dbsMachine, dbsLocal, remain, taskType, minSize, maxSize);
        taskConfig.compareTimestamp(new Date().getTime());
        taskConfig.setType("local");
        // 添加到node列表
        TaskCache.addTaskConfig(taskConfig);

        // 任务执行状态
        List<Param> list = in.getBody().getChilds("taskstate");
        for (Param p : list) {
            // 任务编号
            String code = p.getString("@code");
            // 任务执行时间
            long sysTime = p.getLong("@sysTime");
            TaskCache.putTasknodeMap(code, dbsMachine, sysTime);
            TaskCache.getTaskInfo(code).putNodeType(dbsMachine, 2);
            TaskCache.getTaskInfo(code).putNodeLast(dbsMachine, sysTime);
        }
        if (!list.isEmpty()) {
            TaskCache.compareTimestamp(TaskCache.getTimestamp() + 100);
        }
    }
}
