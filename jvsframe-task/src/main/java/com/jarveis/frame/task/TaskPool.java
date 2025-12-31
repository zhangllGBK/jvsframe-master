package com.jarveis.frame.task;

import com.jarveis.frame.util.Param;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 任务池
 *
 * @author liuguojun
 */
public final class TaskPool {

    private static final Logger log = LoggerFactory.getLogger(TaskPool.class);

    private static TaskPool instance;
    private int minSize;
    private int maxSize;
    private AtomicInteger poolSize = new AtomicInteger(0); // 使用中的线程
    private TaskQueue taskQueue;
    private TaskMonitor taskMonitor;
    private ConcurrentLinkedQueue<TaskThread> idleThreads;

    /**
     * 构造方法
     *
     * @param minSize 最小线程数
     * @param maxSize 最大线程数
     */
    private TaskPool(int minSize, int maxSize) {
        this.minSize = minSize;
        this.maxSize = maxSize;

        taskMonitor = new TaskMonitor(this);
        taskMonitor.start();

        taskQueue = new TaskQueue(this);
        taskQueue.start();

        idleThreads = new ConcurrentLinkedQueue<>();
        for (int i = 0; i < this.minSize; i++) {
            TaskThread taskThread = new TaskThread();
            taskThread.start();
            push(taskThread);
            pushIdle(taskThread);
        }
    }

    /**
     * 获取线程池实例
     *
     * @return TaskPool
     */
    public static TaskPool getInstance(TaskDaemon taskDaemon) {
        if (instance == null) {
            synchronized (TaskPool.class) {
                if (instance == null) {
                    instance = new TaskPool(taskDaemon.getMinSize(), taskDaemon.getMaxSize());
                }
            }
        }
        return instance;
    }

    /**
     * 获取任务监视器
     *
     * @return TaskMonitor
     */
    public TaskMonitor getTaskMonitor() {
        return taskMonitor;
    }

    /**
     * 获取任务监视器
     *
     * @return TaskMonitor
     */
    public TaskQueue getTaskQueue() {
        return taskQueue;
    }

    /**
     * 获取最小线程数
     *
     * @return int
     */
    public int getMinSize() {
        return minSize;
    }

    /**
     * 获取最大线程数
     *
     * @return int
     */
    public int getMaxSize() {
        return maxSize;
    }

    /**
     * 获取剩余线程数
     *
     * @return int
     */
    public int getRemainSize() {
        if (idleThreads.size() >= minSize) {
            return maxSize;
        }
        return maxSize - poolSize.get();
    }

    /**
     * 添加线程
     *
     * @param taskThread 任务线程
     */
    public void push(TaskThread taskThread) {
        if (log.isDebugEnabled()) {
            log.debug("add thread(" + taskThread.getId() + ") to real-time thread pool.");
        }
        poolSize.incrementAndGet();
    }

    /**
     * 删除线程
     *
     * @param taskThread 任务线程
     */
    public void remove(TaskThread taskThread) {
        if (log.isDebugEnabled()) {
            log.debug("remove thread(" + taskThread.getId() + ") of real-time thread pool.");
        }
        poolSize.decrementAndGet();
    }

    /**
     * 添加空闲线程
     *
     * @param taskThread 任务线程
     */
    public void pushIdle(TaskThread taskThread) {
        if (idleThreads.isEmpty()) {
            if (log.isDebugEnabled()) {
                log.debug("add thread(" + taskThread.getId() + ") empty  to idle thread pool.");
            }
            idleThreads.offer(taskThread);
            remove(taskThread);
        } else if (!idleThreads.contains(taskThread)) {
            if (log.isDebugEnabled()) {
                log.debug("add thread(" + taskThread.getId() + ") not contains  to idle thread pool.");
            }
            idleThreads.offer(taskThread);
            remove(taskThread);
        }
    }

    /**
     * 删除空闲线程
     *
     * @param taskThread 任务线程
     */
    public void removeIdle(TaskThread taskThread) {
        if (log.isDebugEnabled()) {
            log.debug("remove thread(" + taskThread.getId() + ") of idle thread pool.");
        }
        idleThreads.remove(taskThread);
    }

    /**
     * 通知有空闲线程
     */
    public void notifyIdleTaskThread(TaskThread taskThread) {
        pushIdle(taskThread);
    }

    /**
     * 是否有空闲线程
     *
     * @return boolean
     */
    public boolean hasIdleTaskThread() {
        return ((!idleThreads.isEmpty()) || (poolSize.get() < maxSize));
    }

    /**
     * 获取空闲线程
     *
     * @return TaskThread
     */
    public TaskThread getIdleTaskThread() {
        TaskThread taskThread = null;

        // 获取空闲的线程
        if (!idleThreads.isEmpty()) {
            taskThread = idleThreads.poll();
            if (log.isDebugEnabled()) {
                log.debug("get thread(" + taskThread.getId() + ") for idle thread pool.");
            }
            return taskThread;
        }

        // 验证线程池是否已满
        if (poolSize.get() < maxSize) {
            taskThread = new TaskThread();
            taskThread.start();
            if (log.isDebugEnabled()) {
                log.debug("create a new thread(" + taskThread.getId() + ")");
            }
            push(taskThread);
        }

        return taskThread;
    }

    /**
     * 执行任务
     *
     * @param task 任务
     */
    public void runTask(ITask task) {
        if (task.getTaskInfo() != null) {
            task.getTaskInfo().setStatus(task.getSysTime(), ITask.Status.Start);
        }
        taskQueue.addTask(task);
    }

    @Override
    public String toString() {
        String str = "";
        try {
            Param out = new Param(Param.RESP);
            for (TaskThread taskThread : idleThreads) {
                String taskStr = taskThread.toString();
                if (!StringUtils.isEmpty(taskStr)) {
                    Param taskOut = new Param(taskStr);
                    Param idleThreadParam = out.getBody().addParam("idleThreads");
                    idleThreadParam.setPropertys(taskOut.getBody().getPropertys());
                    Param taskOutChild = taskOut.getBody().getChild("task");
                    if (taskOutChild != null) {
                        idleThreadParam.addParam("task").setPropertys(taskOutChild.getPropertys());
                    }
                }
            }
            out.getBody().setProperty("@minSize", minSize);
            out.getBody().setProperty("@maxSize", maxSize);
            out.getBody().setProperty("@poolSize", poolSize.get());
            out.getBody().setProperty("@idlethreadSize", idleThreads.size());
            return out.toXmlString();
        } catch (Throwable t) {
            log.error(" ITask  error", t);
        }
        return str;
    }
}