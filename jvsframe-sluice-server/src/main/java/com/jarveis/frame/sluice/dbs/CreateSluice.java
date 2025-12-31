package com.jarveis.frame.sluice.dbs;

import com.jarveis.frame.dbs.Service;
import com.jarveis.frame.dbs.ServiceCode;
import com.jarveis.frame.dbs.ant.Before;
import com.jarveis.frame.dbs.ant.Function;
import com.jarveis.frame.sluice.SluiceCache;
import com.jarveis.frame.sluice.SluiceFormat;
import com.jarveis.frame.sluice.core.bean.PolicyRule;
import com.jarveis.frame.util.Param;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 添加规则配置
 *
 * @author liuguojun
 * @since 2020-06-17
 */
@Function(code = ServiceCode.SLUICE_CREATE_SERVICE)
@Before(filters = "risk")
public class CreateSluice implements Service {

	private static final Logger log = LoggerFactory.getLogger(CreateSluice.class);

	@Override
	public Param callService(Param in) {
		Param out = null;

		try {
			if (log.isDebugEnabled()) {
				log.debug(in.toXmlString());
			}
			
			out = new Param(Param.RESP);

			// 封装请求参数
			long currentTime = System.currentTimeMillis();
			String sulice = in.getBody().getString("@sulice"); // rule | variable
			if ("rule".equals(sulice)) {
				// 添加规则
				String funcId = in.getBody().getString("@serviceCode"); // 服务编码
				String rank = in.getBody().getString("@rank"); // before | after
				String exp = in.getBody().getString("@exp"); // 表达式
				String errcode = in.getBody().getString("@errcode"); // 错误编码
				String errdesc = in.getBody().getString("@errdesc"); // 错误描述
				String priority = in.getBody().getString("@priority"); // 优选级

				PolicyRule policyRule = new PolicyRule();
				policyRule.setFuncId(funcId);
				policyRule.setRank(rank);
				policyRule.setExp(exp);
				policyRule.setErrcode(NumberUtils.toInt(errcode, 0));
				policyRule.setErrdesc(errdesc);
				policyRule.setPriority(NumberUtils.toInt(priority, 0));

				// 更新配置最新时间
				SluiceCache.compareTimestamp(currentTime);
				// 添加风控规则
				SluiceCache.putPolicyRule(policyRule);
				SluiceCache.putServiceRule(funcId, policyRule.getId());
				// 持久化缓存数据
				new SluiceFormat().format();
				out.getBody().addCDATA(Param.ERROR_SUCCESS);
			} else if ("variable".equals(sulice)) {
				String funcId = in.getBody().getString("@serviceCode"); // 服务编码
				String name = in.getBody().getString("@name"); // before | after
				String value = in.getBody().getString("@value"); // 表达式
				String type = in.getBody().getString("@type"); // 错误编码

				// 更新配置最新时间
				SluiceCache.compareTimestamp(currentTime);
				// 添加变(常)量
				SluiceCache.putVariable(type, name, value);
				// 持久化缓存数据
				new SluiceFormat().format();
				out.getBody().addCDATA(Param.ERROR_SUCCESS);
			} else {
				out.getBody().addCDATA(String.valueOf(Param.ERROR_EXCEPTION));
			}

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

}
