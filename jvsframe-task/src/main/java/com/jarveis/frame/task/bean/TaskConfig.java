package com.jarveis.frame.task.bean;

import com.jarveis.frame.util.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * task配置信息
 */
public class TaskConfig implements Serializable {

    private static final Logger log = LoggerFactory.getLogger(TaskConfig.class);

    private long time = 6000;

    private String dbsmachine; // node编号
    private String dbslocal; // node地址
    private int remain; // 空闲线程
    private int minSize; // 连接池最小线程数
    private int maxSize; // 连接池最大线程数
    /**
     * 任务支持类型
     */
    private String taskType; // 支持任务类型
    private long timestamp = 0L; //更新时间

    private String type;// 本地local,remote

    public TaskConfig() {
    }

    public TaskConfig(String dbsmachine, String dbslocal, int remain, String taskType, int minSize, int maxSize) {
        this.dbsmachine = dbsmachine;
        this.dbslocal = dbslocal;
        this.remain = remain;
        this.taskType = taskType;
        this.minSize = minSize;
        this.maxSize = maxSize;
    }

    /**
     * 比较时间戳, 如果传入的时间比系统时间大，则返回true(并重置系统时间为传入时间)，否则返回false；时间单位：ms
     *
     * @param currentTime 更新时间
     * @return 是否替换成功
     * @since 2020-06-17
     */
    public boolean compareTimestamp(long currentTime) {
        if (timestamp < currentTime) {
            timestamp = currentTime;
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取系统的时间戳
     *
     * @return 文件更新时间
     */
    public long getTimestamp() {
        return timestamp;
    }

    public boolean getState() {
        // 当前时间小于更新时间加上心跳时间
        return new Date().getTime() < timestamp + time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDbsmachine() {
        return dbsmachine;
    }

    public void setDbsmachine(String dbsmachine) {
        this.dbsmachine = dbsmachine;
    }

    public String getDbslocal() {
        return dbslocal;
    }

    public void setDbslocal(String dbslocal) {
        this.dbslocal = dbslocal;
    }

    public int getRemain() {
        return remain;
    }

    public void setRemain(int remain) {
        this.remain = remain;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public int getMinSize() {
        return minSize;
    }

    public void setMinSize(int minSize) {
        this.minSize = minSize;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public Map<String, Object> getProperties() {
        HashMap<String, Object> properties = new HashMap<>();

        properties.put("dbsmachine", dbsmachine);
        properties.put("dbslocal", dbslocal);
        properties.put("remain", remain);
        properties.put("tasktype", taskType);
        properties.put("timestamp", timestamp);
        properties.put("minsize", minSize);
        properties.put("maxsize", maxSize);

        return properties;
    }

    @Override
    public String toString() {
        String str = "";
        try {
            Param out = new Param(Param.RESP);
            out.getBody().setProperty("@dbsmachine", dbsmachine);
            out.getBody().setProperty("@dbslocal", dbslocal);
            out.getBody().setProperty("@remain", remain);
            out.getBody().setProperty("@tasktype", taskType);
            out.getBody().setProperty("@timestamp", timestamp);
            out.getBody().setProperty("@minsize", minSize);
            out.getBody().setProperty("@maxsize", maxSize);
            out.getBody().setProperty("@timestamp", timestamp);
            str = out.toXmlString();
        } catch (Throwable t) {
            log.error(" ITask  error", t);
        }
        return str;
    }

}
