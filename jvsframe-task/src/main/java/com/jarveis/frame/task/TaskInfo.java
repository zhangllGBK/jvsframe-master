package com.jarveis.frame.task;

import com.jarveis.frame.task.bean.TaskConfig;
import com.jarveis.frame.util.Param;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 计划实体类
 *
 * @author liuguojun
 */
public class TaskInfo implements Serializable {

    private static final Logger log = LoggerFactory.getLogger(TaskInfo.class);

    private String code;
    private String sleepTime; // 任务执行间隔 或 cron表达式
    private String param; // 任务执行时需要的参数
    private ITask.Status status = ITask.Status.Init; // 任务状态
    private long runTime; // 下一次的运行时间
    private String clazz; // 执行处理类
    private String[] nodes; // 任务执行目标服务编号
    private Map<String, Integer> nodeType; // 任务发布状态 value 0待发布，1已发布，2已执行
    private Map<String, Long> nodeLast; // 任务对应node最后执行时间
    private long sysTime; // 系统时间

    public TaskInfo(String code, String sleepTime, String param) {
        this.code = code;
        this.sleepTime = sleepTime;
        this.param = param;
    }

    public TaskInfo(TaskInfo taskInfo) {
        this.code = taskInfo.getCode();
        this.sleepTime = taskInfo.getSleepTime();
        this.param = taskInfo.getParam();
        this.status = taskInfo.getStatus();
        this.runTime = taskInfo.getRunTime();
        this.clazz = taskInfo.getClazz();
        this.nodes = taskInfo.getNodes();
        this.nodeType = taskInfo.nodeType;
        this.sysTime = taskInfo.sysTime;
    }

    public TaskInfo(String code, String sleepTime, String param, long runTime, String clazz) {
        this.code = code;
        this.sleepTime = sleepTime;
        this.param = param;
        this.runTime = runTime;
        this.clazz = clazz;
    }

    /**
     * 任务编码
     *
     * @return
     */
    public String getCode() {
        return code;
    }

    /**
     * 任务的调度时间
     *
     * @return
     */
    public String getSleepTime() {
        return sleepTime;
    }

    /**
     * 设置任务的调度时间
     * @param sleepTime
     */
    public void setSleepTime(String sleepTime) {
        this.sleepTime = sleepTime;
    }

    /**
     * 获取任务执行时所需的参数
     *
     * @return
     */
    public String getParam() {
        return param;
    }

    /**
     * 设置任务执行时所需的参数
     *
     * @return
     */
    public void setParam(String param) {
        this.param = param;
    }

    /**
     * 获取任务的状态
     *
     * @return
     */
    public ITask.Status getStatus() {
        return status;
    }

    /**
     * 设置任务的状态
     * 
     * @param sysTime
     * @param status
     */
    public void setStatus(long sysTime, ITask.Status status) {
        this.status = status;
        if (status == ITask.Status.Init || status == ITask.Status.Sleep) {
            setRunTime(sysTime);
        }
    }

    public long getRunTime() {
        return runTime;
    }

    private void setRunTime(long runTime) {
        int len = sleepTime.length();
        if (len > 1) {
            String[] arr =StringUtils.split(sleepTime, ' ');
            if (arr.length < 2) {
                char c = sleepTime.charAt(len - 1);
                int n = Integer.parseInt(sleepTime.substring(0, len - 1), 10);

                if (c == 's') {
                    runTime = runTime + n * 1000L;
                } else if (c == 'm') {
                    runTime = runTime + n * 60000L;
                } else if (c == 'h') {
                    runTime = runTime + n * 3600000L;
                } else if (c == 'd') {
                    runTime = runTime + n * 86400000L;
                } else {
                    runTime = Long.MAX_VALUE;
                }
            } else {
                try {
                    CronGenerator generator = new CronGenerator(sleepTime);
                    if (runTime == 0L) {
                        runTime = System.currentTimeMillis();
                    }
                    Date next = generator.next(new Date(runTime));
                    runTime = next.getTime();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        } else {
            runTime = Long.MAX_VALUE;
        }

        this.runTime = runTime;
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public String[] getNodes() {
        return nodes;
    }

    public void setNodes(String[] nodeArr) {
        if (nodeArr != null && nodeArr.length > 0) {
            this.nodes = new String[nodeArr.length];
            System.arraycopy(nodeArr, 0, this.nodes, 0, nodeArr.length);
        }
    }

    public void putNode(String node) {
        if (this.nodes == null) {
            this.nodes = new String[]{node};
        } else {
            Arrays.asList(nodes).add(node);
        }
    }

    public boolean containsNode(String node) {
        if (this.nodes == null) {
            return false;
        }
        return Arrays.asList(nodes).contains(node);
    }

    public Map<String, Integer> getNodeType() {
        if (nodeType == null) {
            nodeType = new HashMap<>();
        }
        return nodeType;
    }

    public void setNodeType(Map<String, Integer> nodeType) {
        this.nodeType = nodeType;
    }

    public void putNodeType(String node, Integer type) {
        if (nodeType == null) {
            nodeType = new HashMap<>();
        }
        nodeType.put(node, type);
    }

    public void initNodeType() {
        String dbsmachine = "";
        for (TaskConfig taskNode : TaskCache.listTaskConfig()) {
            if (taskNode.getState()) {
                dbsmachine += taskNode.getDbsmachine() + ",";
            }
        }
        if (!StringUtils.isEmpty(dbsmachine)) {
            String[] nodes = dbsmachine.substring(0, dbsmachine.length() - 1).split(",");
            setNodes(nodes);
        } else {
            this.nodes = null;
            this.nodeType = new HashMap<>();
        }
        if (this.nodes != null) {
            this.nodeType = new HashMap<>();
            for (String key : this.nodes) {
                nodeType.put(key, 0);
            }
        }

    }

    public long getSysTime() {
        return sysTime;
    }

    public void setSysTime(long sysTime) {
        this.sysTime = sysTime;
    }

    public Map<String, Long> getNodeLast() {
        if (nodeLast == null) {
            nodeLast = new HashMap<>();
        }
        return nodeLast;
    }

    public void setNodeLast(Map<String, Long> nodeLast) {
        this.nodeLast = nodeLast;
    }

    public void putNodeLast(String node, Long lastTime) {
        if (nodeLast == null) {
            nodeLast = new HashMap<>();
        }
        nodeLast.put(node, lastTime);
    }

    @Override
    public String toString() {
        String str = "";
        try {
            Param out = new Param(Param.RESP);
            out.getBody().setProperty("@code", code);
            out.getBody().setProperty("@sleepTime", sleepTime);
            out.getBody().setProperty("@status", status.toString());
            out.getBody().setProperty("@runTime", runTime);
            out.getBody().setProperty("@param", param);
            out.getBody().setProperty("@clazz", clazz);
            out.getBody().setProperty("@nodes", StringUtils.join(nodes, "|"));
            String key = "";
            String value = "";
            if (nodeType != null && !nodeType.isEmpty()) {
                key = StringUtils.join(nodeType.keySet().toArray(), "|");
                value = StringUtils.join(nodeType.values().toArray(), "|");
            }
            out.getBody().setProperty("@nodetypekey", key);
            out.getBody().setProperty("@nodetypeval", value);
            key = "";
            value = "";
            if (nodeLast != null && !nodeLast.isEmpty()) {
                key = StringUtils.join(nodeLast.keySet().toArray(), "|");
                value = StringUtils.join(nodeLast.values().toArray(), "|");
            }
            out.getBody().setProperty("@nodelastkey", key);
            out.getBody().setProperty("@nodelastval", value);
            out.getBody().setProperty("@sysTime", sysTime);
            return out.toXmlString();
        } catch (Throwable t) {
            log.error(" ITask  error", t);
        }
        return str;
    }
}
