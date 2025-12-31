package com.jarveis.frame.dbs.task;

import com.jarveis.frame.dbs.DbsCache;
import com.jarveis.frame.dbs.DbsConst;
import com.jarveis.frame.dbs.ServiceWrapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Calendar;
import java.util.List;

/**
 * 服务自检
 * <p>
 * 检查从远程获取的服务，是否一直处于活跃状态；
 * 如果远程服务在自检时间(@see com.jarveis.frame.dbs.DbsConst#DBS_SELF_CHECK_TIME)内，处于不活跃状态，则将远程服务从本地服务中踢出。
 * </p>
 *
 * @author liuguojun
 * @since 2020-06-03
 */
public class ServiceSelfCheck {

    private long selfCheckTime;

    /**
     * 初始化
     */
    public void init(){
        String time = DbsCache.getConst(DbsConst.DBS_SELF_CHECK_TIME);
        selfCheckTime = NumberUtils.toInt(time, 30000);
    }

    /**
     *
     */
    public void execute() {
        // 当前系统时间
        long currentTime = Calendar.getInstance().getTimeInMillis();
        // 当前节点的所有服务（包含已同步的其它节点服务）
        List<String> allServiceKeys = DbsCache.listServiceKey();
        // 构建同步到master的服务
        for (String sk : allServiceKeys) {
            ServiceWrapper wrapper = DbsCache.getService(sk);
            if (wrapper == null) {
                // 异常服务
                continue;
            }
            if (StringUtils.isNumeric(sk) && Integer.parseInt(sk) < 10000) {
                // 如果是系统内服务，则不需要检查
                continue;
            }
            if (wrapper.isLocalService()) {
                // 如果是本地服务，则不需要检查
                continue;
            }
            wrapper.checkRemoteService(currentTime, selfCheckTime);
        }
    }

}
