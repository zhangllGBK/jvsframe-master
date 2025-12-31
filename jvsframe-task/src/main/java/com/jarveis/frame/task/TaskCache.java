package com.jarveis.frame.task;

import com.jarveis.frame.task.bean.TaskConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 缓存
 *
 * @author zhanglala
 * @create 2020-12-04
 */
public class TaskCache {

    private static final Logger log = LoggerFactory.getLogger(TaskCache.class);

    private static long timestamp = 0L;

    /**
     * 配置信息
     */
    private static TaskConfig taskConfig;

    /**
     * node服务器列表
     */
    private static Map<String, TaskConfig> taskConfigMap = new HashMap<>();

    /**
     * 任务列表
     */
    private static Map<String, TaskInfo> taskInfoMap = new HashMap<>();

    /**
     * 任务状态
     */
    private static Map<String, Integer> taskStateMap = new HashMap<>();

    /**
     * 任务对应node执行次数
     */
    private static Map<String, Map<String, List<Long>>> tasknodeMap = new HashMap<>();

    /**
     * 比较时间戳, 如果传入的时间比系统时间大，则返回true(并重置系统时间为传入时间)，否则返回false；时间单位：ms
     *
     * @param currentTime
     * @return
     * @since 2020-06-17
     */
    public static boolean compareTimestamp(long currentTime) {
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
     * @return
     */
    public static long getTimestamp() {
        return timestamp;
    }

    public static TaskConfig getTaskConfig() {
        if (taskConfig == null) {
            taskConfig = new TaskConfig();
        }
        return taskConfig;
    }

    public static void setTaskConfig(TaskConfig taskConfig) {
        TaskCache.taskConfig = taskConfig;
    }

    /**
     * 添加任务节点的配置, 服务端管理节点端的配置.
     *
     * @param node
     */
    public static void addTaskConfig(TaskConfig node) {
        if (node != null) {
            taskConfigMap.put(node.getDbsmachine(), node);
        }
    }

    /**
     * 获取任务节点的配置
     *
     * @return
     */
    public static Collection<TaskConfig> listTaskConfig() {
        return taskConfigMap.values();
    }

    public static Map<String, TaskInfo> getTaskInfoMap() {
        return taskInfoMap;
    }

    /**
     * @param code 任务编号
     * @return
     */
    public static TaskInfo getTaskInfo(String code) {
        return taskInfoMap.get(code);
    }

    /**
     * @param code 任务编号
     * @return
     */
    public static void removeTaskInfo(String code) {
        taskInfoMap.remove(code);
    }

    public static void putTaskInfoMap(TaskInfo taskInfo) {
        if (taskInfo != null) {
            taskInfoMap.put(taskInfo.getCode(), taskInfo);
        }
    }

    public static void cleanTasks() {
        if (!taskInfoMap.isEmpty()) {
            taskInfoMap.clear();
        }
    }

    public static Map<String, Integer> getTaskStateMap() {
        if (taskStateMap == null) {
            taskStateMap = new HashMap<>();
        }
        return taskStateMap;
    }

    /**
     * 设置状态状态
     * <pre>
     *  type的取值范围(0~3)
     *  0: 初始化
     *  1: 开启
     *  2: 暂停
     *  3: 停止
     * </pre>
     *
     * @param code
     * @param type
     */
    public static void putTaskStateMap(String code, Integer type) {
        if (taskStateMap == null) {
            taskStateMap = new HashMap<>();
        }
        taskStateMap.put(code, type);
    }

    public static void delTaskStateMap(String code) {
        if (taskStateMap != null) {
            taskStateMap.remove(code);
        }
    }

    public static Map<String, Map<String, List<Long>>> getTasknodeMap() {
        if (tasknodeMap == null) {
            tasknodeMap = new HashMap<>();
        }
        return tasknodeMap;
    }

    public static void putTasknodeMap(String code, String node, Long sysTime) {
        if (tasknodeMap == null) {
            tasknodeMap = new HashMap<>();
        }
        Map<String, List<Long>> map = tasknodeMap.get(code);
        if (map == null || map.isEmpty()) {
            map = new HashMap<>();
        }
        List<Long> list = map.get(node);
        if (list == null || list.isEmpty()) {
            list = new LinkedList<Long>();
        }
        if (list.size() > 0 && list.get(0) >= sysTime) {
            return;
        }
        list.add(0, sysTime);
        if (list.size() > 50) {
            list.remove(list.size() - 1);
        }
        map.put(node, list);
        tasknodeMap.put(code, map);
    }
}
