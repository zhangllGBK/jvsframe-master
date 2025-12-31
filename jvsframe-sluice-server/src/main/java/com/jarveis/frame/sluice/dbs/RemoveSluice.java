package com.jarveis.frame.sluice.dbs;

import com.jarveis.frame.dbs.Service;
import com.jarveis.frame.dbs.ServiceCode;
import com.jarveis.frame.dbs.ant.Before;
import com.jarveis.frame.dbs.ant.Function;
import com.jarveis.frame.sluice.SluiceCache;
import com.jarveis.frame.sluice.SluiceFormat;
import com.jarveis.frame.util.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 删除规则配置
 *
 * @author liuguojun
 * @since 2020-06-17
 */
@Function(code = ServiceCode.SLUICE_REMOVE_SERVICE)
@Before(filters = "risk")
public class RemoveSluice implements Service {

	private static final Logger log = LoggerFactory.getLogger(RemoveSluice.class);

	@Override
	public Param callService(Param in) {
		Param out = null;

		try {
			if (log.isDebugEnabled()) {
				log.debug(in.toXmlString());
			}
			
			out = new Param(Param.RESP);

			// 封装请求参数
			String sulice = in.getBody().getString("@sulice"); // rule |
			if ("rule".equals(sulice)) {
				String funcId = in.getBody().getString("@serviceCode"); // 服务编码
				String ruleId = in.getBody().getString("@ruleId");

				// 更新配置最新时间
				SluiceCache.compareTimestamp(System.currentTimeMillis());
				// 删除风控规则
				SluiceCache.removePolicyRule(ruleId);
				SluiceCache.removeServiceRule(funcId, ruleId);
				// 持久化缓存数据
				new SluiceFormat().format();
				out.getBody().addCDATA(Param.ERROR_SUCCESS);
			} else if ("variable".equals(sulice)) {
				String funcId = in.getBody().getString("@serviceCode"); // 服务编码
				String name = in.getBody().getString("@name"); // 变量名称

				// 更新配置最新时间
				SluiceCache.compareTimestamp(System.currentTimeMillis());
				// 删除变(常)量配置
				SluiceCache.removeVariable(name);
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
