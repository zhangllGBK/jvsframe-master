package com.jarveis.frame.task;


import com.jarveis.frame.util.Param;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 任务
 *
 * @author liuguojun
 */
public abstract class ITask {

    private static final Logger log = LoggerFactory.getLogger(ITask.class);

    protected TaskInfo taskInfo;
    protected long sysTime; // 任务执行时的系统时间

    /**
     * 获取任务信息
     */
    public TaskInfo getTaskInfo() {
        return taskInfo;
    }

    /**
     * 设置任务信息
     *
     * @param taskInfo task配置信息
     */
    public ITask setTaskInfo(TaskInfo taskInfo) {
        this.taskInfo = taskInfo;
        return this;
    }

    /**
     * 获取当前任务执行时的系统时间
     *
     * @return 当前时间
     */
    public long getSysTime() {
        return sysTime;
    }

    /**
     * 设置当前任务执行时的系统时间
     *
     * @param sysTime 当前时间
     */
    public void setSysTime(long sysTime) {
        this.sysTime = sysTime;
    }

    /**
     * 任务处理接口方法
     */
    public abstract void execute();

    @Override
    public String toString() {
        String str = "";
        try {
            Param out = new Param(Param.RESP);
            String taskInfoStr = taskInfo.toString();
            if (!StringUtils.isEmpty(taskInfoStr)) {
                Param taskOut = new Param(taskInfoStr);
                out.getBody().setPropertys(taskOut.getBody().getPropertys());
            }
            out.getBody().setProperty("@sysTime", sysTime);
            return out.toXmlString();
        } catch (Throwable t) {
            log.error(" ITask  error", t);
        }
        return str;
    }

    /**
     * 任务状态
     */
    public enum Status {
        Init, /* 初始化 */
        Start, /* 开启 */
        Sleep, /* 暂停 */
        Stop /* 停止 */
    }
}
