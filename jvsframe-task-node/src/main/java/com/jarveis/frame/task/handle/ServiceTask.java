package com.jarveis.frame.task.handle;

import com.jarveis.frame.dbs.ServiceProxy;
import com.jarveis.frame.task.ITask;
import com.jarveis.frame.util.Param;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * dbs请求任务
 * <pre>
 *     {
 *         "head": {
 *
 *         },
 *         "body": {
 *
 *         }
 *     }
 * </pre>
 *
 * @author liuguojun
 * @since 2022-07-14
 */
public class ServiceTask extends ITask {

    private static final Logger log = LoggerFactory.getLogger(ServiceTask.class);

    // String param = "{\"head\":{\"appId\":\"shfcoc.app\",\"dataType\":\"json\",\"device\":\"314a39ffe98e4a9088cc4f9f666a8a35\",\"funcId\":\"90111\",\"token\":\"50c91fc1664547acafd62c11490e92ef\"},\"body\":{}}";
    @Override
    public void execute() {
        try {
            String param = this.getTaskInfo().getParam();
            if (StringUtils.isEmpty(param)) {
                log.debug("ServiceTask param is empty");
                return;
            }
            Param in = new Param(param);
            String funcId = in.getHead().getString("@funcId");
            Param out = ServiceProxy.callService(funcId, in);
            String errcode = out.getHead().getString(Param.LABEL_ERROR);
            if (Param.ERROR_SUCCESS.equals(errcode)) {
                log.debug("ServiceTask succes funcId=" + funcId);
            } else {
                log.debug("ServiceTask fail funcId=" + funcId);
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }
}
