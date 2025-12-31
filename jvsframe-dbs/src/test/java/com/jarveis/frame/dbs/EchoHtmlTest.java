package com.jarveis.frame.dbs;

import org.junit.Assert;
import org.junit.Test;

import com.jarveis.frame.dbs.service.EchoHtml;
import com.jarveis.frame.util.Param;

/**
 * @desc Echo服务单元测试
 * @author liuguojun
 * @create 2018-04-10
 */
public class EchoHtmlTest {

	@Test
	public void testEcho() throws Exception {

		String message = "{";
		message += "'head':{'appId':'8888','appVersion':'4.0','device':'a7bf1feda8124fd7a15b302691ba164f','token':'dbded8a69c9a41d4908a24f5c58ae419','funcId':'10002','dataType':'html'},";
		message += "'body':{'name':'Tom'}";
		message += "}";

		Param in = new Param(message);

		EchoHtml echo = new EchoHtml();
		Param out = echo.callService(in);

		// 验证返回编码
		Assert.assertEquals(Param.ERROR_SUCCESS, out.getHead().getString(Param.LABEL_ERROR));

		// 验证返回内容
		String cdata = out.getBody().getCDATA();
		Assert.assertEquals("Hello,Tom", cdata);
	}
}
