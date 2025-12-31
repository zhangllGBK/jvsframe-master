package com.jarveis.frame.task;

import com.jarveis.frame.util.Param;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 任务监视器
 *
 * @author liuguojun
 */
public class TaskMonitor extends Thread {

    private static final Logger log = LoggerFactory.getLogger(TaskMonitor.class);

    private int allowIdleSecond = 5000; // 允许空闲秒数
    private int refreshMS = 100; // 刷新时间
    private CopyOnWriteArrayList<TaskThread> taskThreads; // 监视的线程集
    private TaskPool taskPool;
    private boolean isWait;

    public TaskMonitor(TaskPool taskPool) {
        super("TaskMonitor");
        this.taskPool = taskPool;
        this.taskThreads = new CopyOnWriteArrayList<>();
        this.isWait = true;
    }

    /**
     * 添加监视的工作
     *
     * @param taskThread 任务线程
     */
    public void push(TaskThread taskThread) {
        taskThread.setIdleSecond(0);
        int i = taskThreads.indexOf(taskThread);
        if (i >= 0) {
            if (log.isDebugEnabled()) {
                log.debug("reset idle time of thread(" + taskThread.getId() + ")");
            }
            taskThreads.set(i, taskThread);
        } else {
            if (log.isDebugEnabled()) {
                log.debug("add thread(" + taskThread.getId() + ") to watch queue");
            }
            taskThreads.add(taskThread);
        }
        if (isWait) {
            isWait = false;
        }
    }

    /**
     * 删除空闲时间较长的线程
     *
     * @param taskThread 任务线程
     */
    public void remove(TaskThread taskThread) {
        if (log.isDebugEnabled()) {
            log.debug("taskThreads number is " + taskThreads.size() + "   " + " taskPool minsize = " + taskPool.getMinSize());
        }
        if (taskThreads.size() > taskPool.getMinSize()) {
            destory(taskThread);
        }
    }

    /**
     * 销毁线程
     *
     * @param taskThread 任务线程
     */
    public void destory(TaskThread taskThread) {
        taskThread.notifyStop();
        taskThreads.remove(taskThread);
        taskPool.removeIdle(taskThread);
        taskPool.remove(taskThread);
    }

    public void run() {
        while (true) {
            try {
                if (log.isDebugEnabled()) {
                    log.debug("monitor threads num is " + taskThreads.size());
                }

                for (TaskThread taskThread : taskThreads) {
                    if (taskThread.getWorkState() == 0) {
                        // 验证是否超过系统定义的空闲时间
                        int idleSecond = taskThread.getIdleSecond();
                        // 空闲时间＝累加每次刷新时间
                        idleSecond = idleSecond + refreshMS;
                        taskThread.setIdleSecond(idleSecond);
                        if (idleSecond >= allowIdleSecond) {
                            remove(taskThread);
                            continue;
                        }
                        // 将运行线程放置到空闲线程队列
                        taskPool.pushIdle(taskThread);
                    } else if (taskThread.getWorkState() < 0) {
                        destory(taskThread);
                    }
                }

                Thread.sleep(refreshMS);
            } catch (InterruptedException ex) {
                log.error(ex.getMessage(), ex);
            }
        }
    }

    @Override
    public String toString() {
        String str = "";
        try {
            Param out = new Param(Param.RESP);
            for (TaskThread taskThread : taskThreads) {
                String taskStr = taskThread.toString();
                if (!StringUtils.isEmpty(taskStr)) {
                    Param taskOut = new Param(taskStr);
                    Param taskBodyOut = out.getBody().addParam("taskThreads");
                    taskBodyOut.setPropertys(taskOut.getBody().getPropertys());
                    Param taskOutChild = taskOut.getBody().getChild("task");
                    if (taskOutChild != null) {
                        taskBodyOut.addParam("task").setPropertys(taskOutChild.getPropertys());
                    }
                }
            }
            out.getBody().setProperty("@allowIdleSecond", allowIdleSecond);
            out.getBody().setProperty("@isWait", isWait);
            out.getBody().setProperty("@refreshMS", refreshMS);
            return out.toXmlString();
        } catch (Throwable t) {
            log.error(" ITask  error", t);
        }
        return str;
    }
}
