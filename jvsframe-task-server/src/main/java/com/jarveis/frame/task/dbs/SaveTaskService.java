package com.jarveis.frame.task.dbs;

import com.jarveis.frame.dbs.Service;
import com.jarveis.frame.dbs.ServiceCode;
import com.jarveis.frame.dbs.ant.Function;
import com.jarveis.frame.task.*;
import com.jarveis.frame.util.Param;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * 发布任务展示
 *
 * @author zhangll
 * @since 2020-12-23
 */
@Function(code = ServiceCode.TASK_PUBLISH_SAVE_SERVICE)
public class SaveTaskService implements Service {

    private static final Logger log = LoggerFactory.getLogger(SaveTaskService.class);

    @Override
    public Param callService(Param in) {
        Param out = null;

        try {
            if (log.isDebugEnabled()) {
                log.debug(in.toXmlString());
            }
            out = new Param(Param.RESP);
            String code = in.getBody().getString("@code");
            String sleeptime = in.getBody().getString("@sleeptime");
            long runtime = in.getBody().getLong("@runtime");
            String param = in.getBody().getString("@param");
            String clazz = in.getBody().getString("@clazz");
            String node = in.getBody().getString("@node");
            Map<String, TaskInfo> taskInfoMap = TaskCache.getTaskInfoMap();
            if (taskInfoMap.containsKey(code)) {
                out.getHead().setProperty(Param.LABEL_ERROR, Param.ERROR_EXCEPTION);
                return out;
            }
            TaskInfo taskInfo = new TaskInfo(code, sleeptime, param);
            taskInfo.setStatus(runtime, ITask.Status.Init); // 初始化任务执行状态
            taskInfo.setNodes(StringUtils.split(node, "|"));
            if (StringUtils.isEmpty(clazz)) {
                out.getHead().setProperty(Param.LABEL_ERROR, Param.ERROR_EXCEPTION);
                return out;
            }
            taskInfo.setClazz(clazz);
            ITask task = new ITask() {
                    @Override
                    public void execute() {

                    }
                }.setTaskInfo(taskInfo);
            task.getTaskInfo().initNodeType();
            TaskCache.putTaskInfoMap(task.getTaskInfo());
            new TaskFormat().format();
            TaskDaemon.getInstance().addTask(task);
            TaskCache.compareTimestamp(TaskCache.getTimestamp() + 100);
            out.getBody().addCDATA(Param.ERROR_SUCCESS);
            return out;
        } catch (Exception ex) {
            if (out != null) {
                out.getHead().setProperty(Param.LABEL_ERROR, Param.ERROR_EXCEPTION);
            }
            log.error(ex.getMessage(), ex);
        }
        return out;
    }
}

