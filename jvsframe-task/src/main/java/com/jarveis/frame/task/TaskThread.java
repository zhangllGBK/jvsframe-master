package com.jarveis.frame.task;

import com.jarveis.frame.util.Param;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * 处理任务的线程
 *
 * @author liuguojun
 */
public final class TaskThread extends Thread {

    private static final Logger log = LoggerFactory.getLogger(TaskThread.class);

    private static AtomicInteger count = new AtomicInteger(1);
    /**
     * -1:stop, 0:wait, 1:running
     */
    private int workState = 0;
    private int idleSecond = 0;
    private ITask task;
    private String name;

    public TaskThread() {
        super("task-" + count.getAndIncrement());
        name = "task-" + count.get();
    }

    /**
     * 设置任务对象
     *
     * @param task 任务
     */
    public void setTask(ITask task) {
        this.task = task;
    }

    /**
     * 获取空闲的时间
     *
     * @return int
     */
    int getIdleSecond() {
        return this.idleSecond;
    }

    /**
     * 设置空闲的时间
     *
     * @param second 空闲时间
     */
    void setIdleSecond(int second) {
        this.idleSecond = second;
    }

    /**
     * 获取工作状态
     *
     * @return int
     */
    int getWorkState() {
        if (log.isDebugEnabled()) {
            log.debug("current thread state(" + (workState == 1 ? "running" : workState == 0 ? "wait" : "stop") + ")");
        }
        return workState;
    }

    /**
     * 通知线程结束
     */
    public synchronized void notifyStop() {
        if (workState > -1) {
            workState = -1;
            notify();
        }
    }

    /**
     * 开始任务
     */
    public synchronized void startTask() {
        workState = 1;
        notify();
    }

    public synchronized void run() {
        try {
            if (log.isDebugEnabled()) {
                log.debug(" start taskthread exectue =" + (task != null ? task.toString() : "") + " worksate = " + workState);
            }
            while (workState > -1) {
                if (workState == 0) {
                    // 如果当前线程空闲，则等待新的任务
                    wait();
                } else {
                    // 执行任务
                    if (task != null) {
                        if (log.isDebugEnabled()) {
                            log.info("task exectue =" + task.toString());
                        }
                        task.execute();
                        if (task.getTaskInfo() != null) {
                            task.getTaskInfo().setStatus(task.getSysTime(), ITask.Status.Sleep);
                            String taskCode = task.getTaskInfo().getCode();
                            if (TaskCache.getTaskStateMap().containsKey(taskCode)) {
                                // node需要推送任务执行状态
                                TaskCache.putTaskStateMap(taskCode, 2);
                                TaskCache.getTaskInfo(taskCode).setSysTime(new Date().getTime());
                            }
                            task = null;
                        }

                    }
                    workState = 0;
                    if (log.isDebugEnabled()) {
                        log.debug(" end taskthread exectue =" + (task != null ? task.toString() : "") + " worksate = " + workState);
                    }
                }
            }
        } catch (Exception ex) {
            if (task != null && task.getTaskInfo() != null) {
                task.getTaskInfo().setStatus(task.getSysTime(), ITask.Status.Sleep);
                if (log.isDebugEnabled()) {
                    log.debug(" Exception taskthread exectue =" + (task != null ? task.toString() : "") + " worksate = " + workState);
                }
                task = null;
                workState = -1;
            }
            log.error(ex.getMessage(), ex);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TaskThread) {
            TaskThread taskThread = (TaskThread) obj;
            return getName().equals(taskThread.getName());
        }
        return false;
    }

    @Override
    public String toString() {
        String str = "";
        try {
            Param out = new Param(Param.RESP);
            out.getBody().setProperty("@name", name);
            out.getBody().setProperty("@workState", workState);
            out.getBody().setProperty("@workStateName", workState == 1 ? "running" : ((workState == 0) ? "wait" : "stop"));
            out.getBody().setProperty("@idleSecond", idleSecond);
            String taskStr = (task == null) ? "" : task.toString();
            if (!StringUtils.isEmpty(taskStr)) {
                Param tempOut = new Param(taskStr);
                out.getBody().addParam("task").setPropertys(tempOut.getBody().getPropertys());
            }

            return out.toXmlString();
        } catch (Throwable t) {
            log.error(" ITask  error", t);
        }
        return str;
    }
}
