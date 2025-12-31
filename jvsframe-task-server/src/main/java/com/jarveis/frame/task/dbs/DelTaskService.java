package com.jarveis.frame.task.dbs;

import com.jarveis.frame.dbs.Service;
import com.jarveis.frame.dbs.ServiceCode;
import com.jarveis.frame.dbs.ant.Function;
import com.jarveis.frame.task.TaskCache;
import com.jarveis.frame.task.TaskDaemon;
import com.jarveis.frame.task.TaskFormat;
import com.jarveis.frame.task.TaskInfo;
import com.jarveis.frame.util.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * 发布任务删除
 *
 * @author zhangll
 * @since 2020-12-23
 */
@Function(code = ServiceCode.TASK_PUBLISH_DEL_SERVICE)
public class DelTaskService implements Service {

    private static final Logger log = LoggerFactory.getLogger(DelTaskService.class);

    @Override
    public Param callService(Param in) {
        Param out = null;

        try {
            if (log.isDebugEnabled()) {
                log.debug(in.toXmlString());
            }
            out = new Param(Param.RESP);
            String code = in.getBody().getString("@code");
            Map<String, TaskInfo> taskInfoMap = TaskCache.getTaskInfoMap();
            if (!taskInfoMap.containsKey(code)) {
                out.getHead().setProperty(Param.LABEL_ERROR, Param.ERROR_EXCEPTION);
                return out;
            }
            TaskCache.removeTaskInfo(code);
            new TaskFormat().format();
            TaskDaemon.getInstance().delTask(code);
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

