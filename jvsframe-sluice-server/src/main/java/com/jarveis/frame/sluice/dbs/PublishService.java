package com.jarveis.frame.sluice.dbs;

import com.jarveis.frame.dbs.Service;
import com.jarveis.frame.dbs.ServiceCode;
import com.jarveis.frame.dbs.ant.Function;
import com.jarveis.frame.sluice.SluiceCache;
import com.jarveis.frame.sluice.core.bean.PolicyRule;
import com.jarveis.frame.util.Param;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

/**
 * 发布配置
 *
 * @author liuguojun
 * @since 2020-06-17
 */
@Function(code = ServiceCode.SLUICE_PUBLISH_SERVICE)
public class PublishService implements Service {

	private static final Logger log = LoggerFactory.getLogger(PublishService.class);

	@Override
	public Param callService(Param in) {
		Param out = null;

		try {
			if (log.isDebugEnabled()) {
				log.debug(in.toXmlString());
			}
			
			out = new Param(Param.RESP);

			// 封装请求参数
			long timestamp = in.getBody().getLong("@timestamp"); // 请求参数
			if (!SluiceCache.compareTimestamp(timestamp)) {
				// 如果订阅端的时间戳小于服务端的时间戳，说明订阅端的数据不是最新数据，则需要将服务端的数据返回给订阅端
				// 添加变(常)量配置
				addVariable(out);
				// 添加服务风控配置
				addPolicy(out);
				// TODO 添加函数
			}
			out.getBody().setProperty("@timestamp", SluiceCache.getTimestamp());
			out.getHead().setProperty(Param.LABEL_ERROR, Param.ERROR_SUCCESS);

			if (log.isDebugEnabled()) {
				log.debug(out.toXmlString());
			}
			
		} catch (Exception ex) {
			if (out != null) {
				out.getHead().setProperty(Param.LABEL_ERROR, Param.ERROR_EXCEPTION);
			}
			log.error(ex.getMessage(), ex);
		}

		return out;
	}

	/**
	 * 添加变(常)量配置
	 *
	 * <pre>
	 *     <variable type="CONST" name="closeTime" value="00:00:00~23:59:59" />
	 * </pre>
	 *
	 * @param out 输出数据包
	 */
	private void addVariable(Param out) {
		Param variablesParam = out.getBody().addParam("variables");
		Set<String> variableKeys = SluiceCache.listVariableKeys();
		for (String key : variableKeys) {
			Param variableParam = variablesParam.addParam("variable");
			String[] arr = SluiceCache.getVariable(key);
			variableParam.setProperty("@type", arr[0]);
			variableParam.setProperty("@name", key);
			variableParam.setProperty("@value", arr[1]);
		}
	}

	/**
	 * 添加服务策略
	 * <pre>
	 *     <policy funcId="10005">
	 * </pre>
	 *
	 * @param out 输出数据包
	 */
	private void addPolicy(Param out) {
		Param policiesParam = out.getBody().addParam("policies");
		Set<String> serviceKeys = SluiceCache.listServiceKeys();
		for (String funcId : serviceKeys) {
			// 系统服务不需要同步到serviceNode
			if (NumberUtils.toInt(funcId, NumberUtils.INTEGER_ZERO) < 10000) {
				continue;
			}
			// 添加服务的策略节点
			Param policieParam = policiesParam.addParam("policy");
			policieParam.setProperty("@funcId", funcId);

			addRule(policieParam, funcId);
		}
	}

	/**
	 * 添加规则
	 *
	 * <pre>
	 *     <rule rank='before' exp='$matchDates(closeTime, $sysdate())' errcode='1001' priority='9' />
	 * </pre>
	 *
	 * @param policieParam 输出数据包
	 * @param funcId 服务标识
	 */
	private void addRule(Param policieParam, String funcId) {
		List<String> serviceRules = SluiceCache.getServiceRules(funcId);
		for (String ruleId : serviceRules) {
			// 添加服务的拦截规则
			Param ruleParam = policieParam.addParam("rule");
			PolicyRule policyRule = SluiceCache.getPolicyRule(ruleId);
			ruleParam.setProperty("@rank", policyRule.getRank());
			ruleParam.setProperty("@exp", policyRule.getExp());
			ruleParam.setProperty("@errcode", String.valueOf(policyRule.getErrcode()));
			ruleParam.setProperty("@errdesc", policyRule.getErrdesc());
			ruleParam.setProperty("@priority", String.valueOf(policyRule.getPriority()));
		}
	}

}
