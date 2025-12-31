package com.jarveis.frame.sluice.task;

import com.jarveis.frame.dbs.DbsCache;
import com.jarveis.frame.dbs.DbsConst;
import com.jarveis.frame.dbs.DbsUtils;
import com.jarveis.frame.dbs.ServiceCode;
import com.jarveis.frame.sluice.SluiceCache;
import com.jarveis.frame.sluice.SluiceFormat;
import com.jarveis.frame.sluice.core.bean.PolicyRule;
import com.jarveis.frame.util.CharacterUtil;
import com.jarveis.frame.util.HttpUtil;
import com.jarveis.frame.util.Param;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订阅服务端的风控规则
 * <pre>
 * SluiceSubscribe用于serviceNode从serviceServer上获取服务的风控规则，同时也用于serviceServer之间进行风控规则的同步。
 *
 * SluiceSubscribe的启动取决于dbs_name_server是否配置。
 *
 * 工作原理：
 * SluiceSubscribe从serviceServer获取风控配置后，会刷新本地的风控配置（PolicyRule），并清除无效的风控配置（PolicyRule）
 *
 * 如何刷新风控配置或清除无效的风控配置，serviceServer在返回风控配置的同时，会返回当前配置的更新时间；
 * 本地如果有相同配置会更新风控配置时间，如果本地中没有更新配置时间的，则说明配置已失效，则会清除。
 * </pre>
 *
 * @author liuguojun
 * @since 2020-06-17
 */
public class SluiceSubscribe {

    private static final Logger log = LoggerFactory.getLogger(SluiceSubscribe.class);

    private String[] servers;

    /**
     * 初始化任务
     */
    public void init() {
        // 服务器节点
        String serviceServer = DbsCache.getConst(DbsConst.DBS_SERVICE_SERVER);

        if (StringUtils.isEmpty(serviceServer)) {
            return;
        }

        // 注册服务器
        servers = StringUtils.split(serviceServer, CharacterUtil.SEPARATOR);
    }

    /**
     * 执行任务
     */
    public void execute() {
        // 同步时请求参数对象
        Param in, out ;
        // 同步时返回消息
        String message;
        Map<String, String> params = new HashMap<String, String>(1);
        try {
            String dbsLocal = DbsCache.getConst(DbsConst.DBS_LOCAL);

            in = new Param(Param.REQ);
            in.getHead().setProperty(Param.LABEL_FUNCID, ServiceCode.SLUICE_PUBLISH_SERVICE);
            in.getHead().setProperty(Param.LABEL_DATATYPE, Param.DT_JSON);
            in.getBody().setProperty("@timestamp", SluiceCache.getTimestamp());

            // 遍历当前集群列表
            for (int i = 0; i < servers.length; i++) {
                if (servers[i].equals(dbsLocal)) {
                    continue;
                }
                params.put("_message", in.toXmlString());
                String remoteNode = DbsUtils.getDbsURI(servers[i]);
                message = HttpUtil.doPost(remoteNode, params);
                if (StringUtils.isNotEmpty(message)) {
                    out = new Param(message);
                    long timestamp = out.getBody().getLong("@timestamp");
                    if (SluiceCache.compareTimestamp(timestamp)){
                        // 如果订阅端的时间戳小于服务端的时间戳，说明订阅端的数据不是最新数据，则需要同步服务端的数据
                        // 更新本地变(常)量
                        updateVariable(out);
                        // 更新服务的风控配置
                        updatePolicy(out);
                        // 清理无效数据
                        SluiceCache.clean();
                        // 持久化缓存数据
                        new SluiceFormat().format();

                        if (log.isDebugEnabled()) {
                            log.debug("订阅远程服务器风控规则成功(" + remoteNode + ")；本地已同步服务端最新数据。");
                        }
                    } else {
                        if (log.isDebugEnabled()) {
                            log.debug("订阅远程服务器风控规则成功(" + remoteNode + ")；未获取最新数据。");
                        }
                    }
                } else {
                    log.warn("订阅远程服务器风控规则失败（" + remoteNode + "）");
                }
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    /**
     * 添加变(常)量配置
     *
     * @param out 数据包
     */
    private void updateVariable(Param out) {
        Param variables = out.getBody().getChild("variables");
        List<Param> variableList = variables.getChilds("variable");
        for (Param variable : variableList) {
            String type = variable.getString("@type"); // 类型
            String name = variable.getString("@name"); // 名称
            String value = variable.getString("@value"); // 值

            SluiceCache.putVariable(type, name, value);
        }
    }

    /**
     * 添加风控配置
     *
     * @param out 数据包
     */
    private void updatePolicy(Param out) {
        Param policies = out.getBody().getChild("policies");
        List<Param> policyList = policies.getChilds("policy");
        for (Param policy : policyList) {
            String funcId = policy.getString("@funcId");
            if (StringUtils.isEmpty(funcId)) {
                log.error(funcId + "未定义funcId属性");
                continue;
            }
            // 获取配置节点下的所有规则
            List<Param> ruleList = policy.getChilds("rule");
            for (Param rule : ruleList) {
                PolicyRule policyRule = addRule(rule);
                policyRule.setFuncId(funcId);
                SluiceCache.putPolicyRule(policyRule);
                // 设置服务的拦截规则
                SluiceCache.putServiceRule(funcId, policyRule.getId());
            }
        }
    }

    /**
     * 添加规则
     *
     * @param rule 规则对象
     * @return 数据包
     */
    private PolicyRule addRule(Param rule) {
        String rank = rule.getString("@rank");
        String exp = rule.getString("@exp");
        String errcode = rule.getString("@errcode");
        String errdesc = rule.getString("@errdesc");
        String priority = rule.getString("@priority");

        PolicyRule policyRule = new PolicyRule();
        policyRule.setRank(rank);
        policyRule.setExp(exp);
        policyRule.setErrcode(NumberUtils.toInt(errcode, 0));
        policyRule.setErrdesc(errdesc);
        policyRule.setPriority(NumberUtils.toInt(priority, 0));
        // 规则的更新时间与sluice配置文件的更新时间一致
        policyRule.setLasttime(SluiceCache.getTimestamp());

        return policyRule;
    }
}
