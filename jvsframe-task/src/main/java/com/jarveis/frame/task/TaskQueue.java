package com.jarveis.frame.task;

import com.jarveis.frame.util.Param;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 任务对列
 *
 * @author liuguojun
 */
public final class TaskQueue extends Thread {

    private static final Logger log = LoggerFactory.getLogger(TaskQueue.class);

    private ConcurrentLinkedQueue<ITask> queue;
    private TaskPool taskPool;
    private TaskMonitor taskMonitor;
    private boolean isWait = false;

    public TaskQueue(TaskPool taskPool) {
        super("TaskQueue");
        this.queue = new ConcurrentLinkedQueue<>();
        this.taskPool = taskPool;
        taskMonitor = taskPool.getTaskMonitor();
    }

    /**
     * 添加任务到队列中
     *
     * @param task 任务
     */
    public synchronized void addTask(ITask task) {
        queue.offer(task);
        if (log.isDebugEnabled()) {
            log.debug("add task to last of queue");
        }
        if (queue.size() > 1000) {
            log.warn("Too many tasks in the queue(size > 1000).");
        }
        if (isWait) {
            isWait = false;
            notify();
        }
    }

    /**
     * 从队列中弹出任务或任务组对象
     *
     * @return Object
     */
    private ITask popTask() {
        ITask task = null;
        if (!queue.isEmpty()) {
            if (log.isDebugEnabled()) {
                log.debug("remove first task for queue.");
            }
            task = queue.poll();
        }
        return task;
    }

    public synchronized void run() {
        try {
            while (true) {
                if (queue.size() == 0) {
                    if (log.isDebugEnabled()) {
                        log.debug("work queue is empty.");
                    }
                    isWait = true;
                    wait();
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug("task number of work queue is " + queue.size() + " queue=" + queue);
                    }
                    if (taskPool.hasIdleTaskThread()) {
                        // 线程池中有空闲线程，获取空闲线程来执行任务
                        TaskThread taskThread = taskPool.getIdleTaskThread();
                        taskThread.setTask(popTask());
                        taskThread.startTask();
                        taskPool.push(taskThread);
                        if (log.isDebugEnabled()) {
                            log.debug("add task to thread and running." + taskThread);
                        }
                        //监视任务线程
                        taskMonitor.push(taskThread);
                    } else {
                        sleep(9);
                    }
                }
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    @Override
    public String toString() {
        String str = "";
        try {
            Param out = new Param(Param.RESP);
            for (ITask task : queue) {
                String taskStr = task.toString();
                if (!StringUtils.isEmpty(taskStr)) {
                    Param taskOut = new Param(taskStr);
                    Param queueParam = out.getBody().addParam("queue");
                    queueParam.setPropertys(taskOut.getBody().getPropertys());
                }
            }
            out.getBody().setProperty("@isWait", isWait);
            return out.toXmlString();
        } catch (Throwable t) {
            log.error(" ITask  error", t);
        }
        return str;
    }
}