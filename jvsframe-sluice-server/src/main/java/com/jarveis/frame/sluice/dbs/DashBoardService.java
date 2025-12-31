package com.jarveis.frame.sluice.dbs;

import com.jarveis.frame.dbs.Service;
import com.jarveis.frame.dbs.ServiceCode;
import com.jarveis.frame.dbs.ant.Before;
import com.jarveis.frame.dbs.ant.Function;
import com.jarveis.frame.sluice.SluiceCache;
import com.jarveis.frame.sluice.core.bean.PolicyRule;
import com.jarveis.frame.util.CharacterUtil;
import com.jarveis.frame.util.Param;
import com.jarveis.frame.util.Resource;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

/**
 *
 * @author liuguojun
 * @since  2020-01-16
 */
@Function(code = ServiceCode.SLUICE_DASH_BOARD_SERVICE)
@Before(filters = "risk")
public class DashBoardService implements Service {

	private static final Logger log = LoggerFactory.getLogger(DashBoardService.class);

	@Override
	public Param callService(Param in) {
		Param out = null;
		try {
			out = new Param(Param.RESP);
			String serviceCode = in.getBody().getString("@serviceCode");
			if (!StringUtils.isEmpty(serviceCode)) {
				StringBuilder rowsBuf = new StringBuilder();
				rowsBuf.append("<p class=\"serviceCode\">服务编码：<span>").append(serviceCode).append("</span></p>");
				rowsBuf.append("<table width=\"100%\" border=\"1\" cellpadding=\"0\" cellspacing=\"0\">");
				rowsBuf.append("<tr>");
				rowsBuf.append("<td width=\"100\">规则</td>");
				rowsBuf.append("<td>表达式</td>");
				rowsBuf.append("<td width=\"100\">错误编码</td>");
				rowsBuf.append("<td width=\"100\">错误描述</td>");
				rowsBuf.append("<td width=\"100\">优先级</td>");
				rowsBuf.append("<td width=\"100\">操作</td>");
				rowsBuf.append("</tr>");
				List<String> rules = SluiceCache.getServiceRules(serviceCode);
				for (String ruleId : rules) {
					PolicyRule rule = SluiceCache.getPolicyRule(ruleId);
					rowsBuf.append("<tr>");
					rowsBuf.append("<td>").append(rule.getRank()).append("</td>");
					rowsBuf.append("<td>").append(rule.getExp()).append("</td>");
					rowsBuf.append("<td>").append(rule.getErrcode()).append("</td>");
					rowsBuf.append("<td>").append(rule.getErrdesc()).append("</td>");
					rowsBuf.append("<td>").append(rule.getPriority()).append("</td>");
					rowsBuf.append("<td><input type=\"button\" value=\"删除\" class=\"delRule\" data-value=\"" + ruleId + "\" /></td>");
					rowsBuf.append("</tr>");
				}
				rowsBuf.append("<tr>");
				rowsBuf.append("<td colspan=\"6\" align=\"center\"><input type=\"button\" value=\"添加规则\" class=\"addRule\" /></td>");
				rowsBuf.append("</tr>");
				rowsBuf.append("</table>");

				rowsBuf.append("<br/><br/>");

				rowsBuf.append("<table width=\"100%\" border=\"1\" cellpadding=\"0\" cellspacing=\"0\">");
				rowsBuf.append("<tr>");
				rowsBuf.append("<td width=\"100\">变量名</td>");
				rowsBuf.append("<td>变量值</td>");
				rowsBuf.append("<td width=\"100\">变量类型</td>");
				rowsBuf.append("<td width=\"100\">操作</td>");
				rowsBuf.append("</tr>");
				Set<String> keys = SluiceCache.listVariableKeys();
				for (String key : keys) {
					String[] arr = SluiceCache.getVariable(key);
					rowsBuf.append("<tr>");
					rowsBuf.append("<td>").append(key).append("</td>");
					rowsBuf.append("<td>").append(arr[1]).append("</td>");
					rowsBuf.append("<td>").append(arr[0]).append("</td>");
					rowsBuf.append("<td><input type=\"button\" value=\"删除\" class=\"delVariable\" data-value=\"" +key + "\" /></td>");
					rowsBuf.append("</tr>");
				}
				rowsBuf.append("<tr>");
				rowsBuf.append("<td colspan=\"4\" align=\"center\"><input type=\"button\" value=\"添加变量\" class=\"addVariable\" /></td>");
				rowsBuf.append("</tr>");
				rowsBuf.append("</table>");

				String tableHtml = rowsBuf.toString();
				String scriptHtml = processScript();
				out.getBody().addCDATA(scriptHtml + tableHtml);
			}

			out.getHead().setProperty(Param.LABEL_ERROR, Param.ERROR_SUCCESS);
			return out;
		} catch (Exception ex) {
			if (out != null) {
				out.getHead().setProperty(Param.LABEL_ERROR, Param.ERROR_EXCEPTION);
			}
			log.error(ex.getMessage(), ex);
		}
		return out;
	}

	/**
	 * 处理脚本
	 *
	 * @return 脚本信息
	 */
	private String processScript() throws Exception {
		StringBuilder script = new StringBuilder();
		script.append("<script src=\"https://cdn.bootcdn.net/ajax/libs/jquery/3.5.1/jquery.min.js\"></script>");
		script.append("<script type=\"text/javascript\">");
		script.append(IOUtils.toString(Resource.getStream("sluice.js"), CharacterUtil.UTF8));
		script.append("</script>");
		return script.toString();
	}

}
