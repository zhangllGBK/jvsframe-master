package com.jarveis.frame.dbs;

import org.junit.Assert;
import org.junit.Test;

import com.jarveis.frame.dbs.service.Echo;
import com.jarveis.frame.util.Param;

/**
 * @desc Echo服务单元测试
 * @author liuguojun
 * @create 2018-04-10
 */
public class EchoTest {

	@Test
	public void testEcho() throws Exception {

		String content = "{";
		content += "'head':{'appId':'8888','appVersion':'4.0','device':'a7bf1feda8124fd7a15b302691ba164f','token':'dbded8a69c9a41d4908a24f5c58ae419','funcId':'10001','dataType':'json'},";
		content += "'body':{'name':'Tom'}";
		content += "}";
		Param in = new Param(content);

		// 调用服务
		Echo echo = new Echo();
		Param out = echo.callService(in);

		// 验证返回编码
		Assert.assertEquals(Param.ERROR_SUCCESS, out.getHead().getString(Param.LABEL_ERROR));

		// 验证返回内容
		Assert.assertEquals("Hello,Tom", out.getBody().getString("@message"));
	}
}
