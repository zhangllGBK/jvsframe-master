package com.jarveis.frame.sluice.dbs;

import com.jarveis.frame.dbs.DbsCache;
import com.jarveis.frame.dbs.ServiceWrapper;
import com.jarveis.frame.sluice.SluiceCache;
import com.jarveis.frame.sluice.SluiceParser;
import com.jarveis.frame.sluice.core.bean.PolicyRule;
import com.jarveis.frame.sluice.core.function.Functions;
import com.jarveis.frame.util.DateUtil;
import com.jarveis.frame.util.Param;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class PolicyTest {
	
	@Before
	public void before() throws Exception {
		DbsCache.putLocalService("10053", new ServiceWrapper("10053"));
		DbsCache.putLocalService("10017", new ServiceWrapper("10017"));
		DbsCache.putLocalService("10037", new ServiceWrapper("10037"));
		SluiceParser parser = new SluiceParser();
		parser.parse();
	}

	@Test
	public void invokeClose() throws Exception {
		int result = 0;

		String message = "{";
		message += "'head':{'funcId':'10053','dataType':'json'},";
		message += "'body':{'source':'h5','lotteryid':'01'}";
		message += "}";

		Param in = new Param(message);
		String funcId = in.getHead().getString(Param.LABEL_FUNCID);
		List<String> rules = SluiceCache.getServiceRules(funcId);
		System.out.println("funcId=" + funcId + ", rules.size()=" + rules.size());
		if (rules.size() > 0) {
			String ruleId = rules.get(0);
			PolicyRule rule = SluiceCache.getPolicyRule(ruleId);
			result = rule.invoke(in);
		}

		long curDate = DateUtil.getDate().getTime();
		long beginDate = Functions.getDate("2019-02-04").getTime();
		long endDate = Functions.getDate("2019-02-10").getTime();
		if (curDate > beginDate && curDate < endDate) {
			Assert.assertEquals(result, 1001);
		} else {
			Assert.assertEquals(result, 0);
		}
	}

	@Test
	public void invokeRegister() throws Exception {
		int result = 0;

		String message = "{";
		message += "'head':{'funcId':'10017','dataType':'json'},";
		message += "'body':{'source':'h5','lotteryid':'01'}";
		message += "}";

		Param in = new Param(message);
		String funcId = in.getHead().getString(Param.LABEL_FUNCID);
		List<String> rules = SluiceCache.getServiceRules(funcId);
		System.out.println("funcId=" + funcId + ", rules.size()=" + rules.size());
		if (rules.size() > 0) {
			String ruleId = rules.get(0);
			PolicyRule rule = SluiceCache.getPolicyRule(ruleId);
			System.out.println("rule1=" + rule.toString());
			result = rule.invoke(in);
		}

		Assert.assertEquals(result, 1002);
	}

	@Test
	public void invokePayment() throws Exception {
		int result = 0;

		String message = "{";
		message += "'head':{'funcId':'10037','dataType':'json'},";
		message += "'body':{'source':'h5','usercode':'10000001'}";
		message += "}";

		Param in = new Param(message);
		String funcId = in.getHead().getString(Param.LABEL_FUNCID);
		List<String> rules = SluiceCache.getServiceRules(funcId);
		System.out.println("funcId=" + funcId + ", rules.size()=" + rules.size());
		if (rules.size() > 0) {
			String ruleId = rules.get(0);
			PolicyRule rule = SluiceCache.getPolicyRule(ruleId);
			System.out.println("rule2=" + rule.toString());
			result = rule.invoke(in);
		}

		Assert.assertEquals(result, 1004);
	}

}
