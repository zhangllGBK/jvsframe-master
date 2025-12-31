package com.jarveis.frame.sluice.dbs;

import com.jarveis.frame.dbs.ant.Interceptor;
import com.jarveis.frame.dbs.filter.Filter;
import com.jarveis.frame.sluice.SluiceCache;
import com.jarveis.frame.sluice.core.bean.PolicyRule;
import com.jarveis.frame.util.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 风控拦截器
 *
 * @author liuguojun
 * @since 2018-07-24
 */
@Interceptor(code = "risk")
public class SluiceFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(SluiceFilter.class);

    @Override
    public int init() {
        return 0;
    }

    @Override
    public int filter(Param in) {
        int errcode = 0;
        try {
            if (log.isDebugEnabled()) {
                log.debug("风控组件对请求处理：开始");
            }
            String tagName = in.getTagName();
            String funcId = in.getHead().getString(Param.LABEL_FUNCID);

            String rank;
            // 获取服务的拦截规则
            List<String> rules = SluiceCache.getServiceRules(funcId);
            for (String ruleId : rules) {
                PolicyRule rule = SluiceCache.getPolicyRule(ruleId);
                rank = rule.getRank();
                if ((Param.REQ.equals(tagName) && "before".equals(rank)) || (Param.RESP.equals(tagName) && "after".equals(rank))) {
                    if (log.isDebugEnabled()) {
                        log.debug(rule.toString());
                    }
                    // 执行拦截规则
                    errcode = rule.invoke(in);
                    if (log.isDebugEnabled()) {
                        log.debug("errcocd=" + errcode);
                    }
                }
                if (errcode > 0) {
                    in.getHead().setProperty(LABEL_SLUICE_ERROR_DESC, rule.getErrdesc());
                    break;
                }
            }
        } finally {
            if (log.isDebugEnabled()) {
                log.debug("风控组件对请求处理：完成");
            }
        }

        return errcode;
    }

    @Override
    public int destory() {
        return 0;
    }

}
