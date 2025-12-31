package com.jarveis.frame.task;

import com.jarveis.frame.util.DateUtil;
import com.jarveis.frame.util.Param;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * 计划后台程序
 *
 * @author liuguojun
 */
public class TaskDaemon extends Thread {

    private static final Logger log = LoggerFactory.getLogger(TaskDaemon.class);

    private static TaskPool taskPool = null;
    private static List<ITask> tasks = new ArrayList<>();
    private static TaskDaemon instance;
    public final long SLEEP = 9L;
    private int minSize; // 连接池最小线程数
    private int maxSize; // 连接池最大线程数
    private String model; // 存储模式
    private String save; // 存储地
    private String update; // 更新地
    private boolean isStart = false;
    private boolean isWait = false;
    private long nowTime = 0L;

    private TaskDaemon(String name) {
        super(name);
    }

    /**
     * 获取单例实例
     *
     * @return 任务主线程
     */
    public static TaskDaemon getInstance() {
        if (instance == null) {
            synchronized (TaskDaemon.class) {
                if (instance == null) {
                    instance = new TaskDaemon("TaskDaemon");
                }
            }
        }

        return instance;
    }

    /**
     * 初始化任务池
     *
     * @param minSize 最小任务数
     * @param maxSize 最大任务数
     */
    public void setTaskPool(int minSize, int maxSize) {
        if (taskPool == null) {
            this.minSize = minSize;
            this.maxSize = maxSize;
            taskPool = TaskPool.getInstance(this);
        }
    }

    /**
     * 连接池最小数
     *
     * @return 最小任务数
     */
    public int getMinSize() {
        return minSize;
    }

    /**
     * 连接池最大数
     *
     * @return 最大任务数
     */
    public int getMaxSize() {
        return maxSize;
    }

    /**
     * 存储模式
     *
     * @return 存储模式
     */
    public String getModel() {
        return model;
    }

    /**
     * 存储地
     *
     * @return 存储路径
     */
    public String getSave() {
        return save;
    }

    /**
     * 更新地
     *
     * @return 更新路径
     */
    public String getUpdate() {
        return update;
    }

    /**
     * 状态
     *
     * @return
     */
    public boolean getisStart() {
        return isStart;
    }

    /**
     * 状态
     *
     * @return
     */
    public boolean getisWait() {
        return isWait;
    }

    /**
     * 获取当前主程序的时间
     *
     * @return 当前时间
     */
    public long getNowTime() {
        return nowTime;
    }

    /**
     * 添加任务
     *
     * @param task 任务
     */
    public void addTask(ITask task) {
        if (task != null && task.getTaskInfo() != null) {
            // 先删除,再添加
            delTask(task.getTaskInfo().getCode());
            tasks.add(task);
            log.info("加载 (code=" + task.getTaskInfo().getCode() + ", class=" + task.getClass().getName() + ") 任务成功！");
        }
    }

    /**
     * 删除任务
     *
     * @param code
     */
    public void delTask(String code) {
        Iterator<ITask> it = tasks.iterator();
        while (it.hasNext()) {
            ITask iTask = it.next();
            if (iTask.getTaskInfo().getCode().equals(code)) {
                it.remove();
                break;
            }
        }
    }

    /**
     * 获取任务
     *
     * @param code 任务编码
     */
    public ITask getTask(String code) {
        ITask task = null;
        for (ITask iTask : tasks) {
            if (iTask.getTaskInfo().getCode().equals(code)) {
                task = iTask;
                break;
            }
        }
        return task;
    }

    /**
     * 获取任务
     */
    public List<ITask> getTask() {
        return tasks;
    }

    /**
     * 清除所有的任务， 清除所有任务后，TaskDaemon会调用wait()方法挂起等待，当TaskDaemon再次调用start()方法时，
     * 会再次唤醒TaskDaemon线程。
     */
    public void cleanTasks() {
        if (!tasks.isEmpty()) {
            tasks.clear();
        }
    }

    /**
     * 重写Thread.start()方法，当TaskDaemon第二次调用start()方法时，不再调用Thread.start()方法，
     * 而是通过notify()方法进行唤醒线程。
     */
    @Override
    public synchronized void start() {
        if (!isStart) {
            super.start();
            isStart = true;
            log.info("Task Daemon is Started.");
        }

        if (isWait) {
            isWait = false;
            log.info("Task Daemon is Notify.");
            notify();
        }
    }

    @Override
    public synchronized void run() {
        // 获取当前时间
        nowTime = 0L;

        while (true) {
            try {
                if (tasks.isEmpty()) {
                    // 主线程没有任务的时候,暂停运行.
                    log.info("Task Daemon is Wait.");
                    isWait = true;
                    wait();
                }
                for (ITask task : tasks) {
                    ITask.Status status = task.getTaskInfo().getStatus();
                    long runTime = task.getTaskInfo().getRunTime();
                    if (status == ITask.Status.Init || (status == ITask.Status.Sleep && nowTime >= runTime)) {
                        task.setSysTime(nowTime);
                        taskPool.runTask(task);
                    }
                }
                if (log.isDebugEnabled()) {
                    log.debug("tasks run  nowTime " + nowTime);
                }
                nowTime += SLEEP;
                Thread.sleep(SLEEP);
            } catch (InterruptedException ex) {
                log.error("后台任务调试线程休眠被异常中断", ex);
                try {
                    Thread.sleep(SLEEP);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Override
    public String toString() {
        String str = "";
        try {
            Param out = new Param(Param.RESP);
            // taskPool
            String taskStr = (taskPool != null) ? taskPool.toString() : "";
            if (!StringUtils.isEmpty(taskStr)) {
                Param taskParam = new Param(taskStr);
                out.getBody().addParam("taskPool").setPropertys(taskParam.getBody().getPropertys());
                List<Param> idleThreads = taskParam.getBody().getChilds("idleThreads");
                if (idleThreads != null && idleThreads.size() > 0) {
                    for (Param taskThread : idleThreads) {
                        if (taskThread != null) {
                            Param taskThreadParam = out.getBody().getChild("taskPool").addParam("idleThreads");
                            taskThreadParam.setPropertys(taskThread.getPropertys());
                            Param tempTaskParam = taskThread.getChild("task");
                            if (tempTaskParam != null) {
                                taskThreadParam.addParam("task").setPropertys(tempTaskParam.getPropertys());
                            }
                        }
                    }
                }
            }
            // taskMonitor
            taskStr = (taskPool != null) ? (taskPool.getTaskMonitor() != null ? taskPool.getTaskMonitor().toString() : "") : "";
            if (!StringUtils.isEmpty(taskStr)) {
                Param taskParam = new Param(taskStr);
                out.getBody().addParam("taskMonitor").setPropertys(taskParam.getBody().getPropertys());
                List<Param> taskThreads = taskParam.getBody().getChilds("taskThreads");
                if (taskThreads != null && taskThreads.size() > 0) {
                    for (Param taskThread : taskThreads) {
                        if (taskThread != null) {
                            Param taskThreadParam = out.getBody().getChild("taskMonitor").addParam("taskThreads");
                            taskThreadParam.setPropertys(taskThread.getPropertys());
                            Param tempTaskParam = taskThread.getChild("task");
                            if (tempTaskParam != null) {
                                taskThreadParam.addParam("task").setPropertys(tempTaskParam.getPropertys());
                            }
                        }
                    }
                }
            }
            // taskQueue
            taskStr = (taskPool != null) ? (taskPool.getTaskQueue() != null ? taskPool.getTaskQueue().toString() : "") : "";
            if (!StringUtils.isEmpty(taskStr)) {
                Param taskParam = new Param(taskStr);
                out.getBody().addParam("taskQueue").setPropertys(taskParam.getBody().getPropertys());
                List<Param> tasks = taskParam.getBody().getChilds("tasks");
                if (tasks != null && tasks.size() > 0) {
                    for (Param taskThread : tasks) {
                        if (taskThread != null) {
                            out.getBody().getChild("taskQueue").addParam("tasks").setPropertys(taskThread.getPropertys());
                        }
                    }
                }
            }
            // taskDaemon
            Param taskDaemonParam = out.getBody().addParam("taskDaemon");
            taskDaemonParam.setProperty("@isStart", isStart);
            taskDaemonParam.setProperty("@nowTime", nowTime);
            taskDaemonParam.setProperty("@isWait", isWait);

            for (ITask task : tasks) {
                String tempTaskStr = task.toString();
                if (!StringUtils.isEmpty(tempTaskStr)) {
                    Param taskOut = new Param(tempTaskStr);
                    taskDaemonParam.addParam("tasks").setPropertys(taskOut.getBody().getPropertys());
                }
            }
            // out节点
            out.getBody().setProperty("@runsystime", DateUtil.getDateStr(new Date(), DateUtil.FORMAT_YMDHMS));
            return out.toXmlString();
        } catch (Throwable t) {
            log.error(" ITask  error", t);
        }
        return str;
    }
}
