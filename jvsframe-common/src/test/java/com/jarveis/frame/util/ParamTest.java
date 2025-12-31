package com.jarveis.frame.util;

import org.junit.Assert;
import org.junit.Test;

public class ParamTest {

	@Test
	public void testInterfaceData() throws Exception {
		String result = "<?xml version='1.0' encoding='gb2312'?><MSG errcode='8002' errdesc='未查询到用户绑定第三方' passwds='FZ4NN4xg9aAD/TXMKIhhyTCiNMcsMTSFbHbQ7u/GaTU9wqVup12whSZ5M209bGaAXEtbCa80P5Lf7wSK1GwqdvnqYSHXW/nkQZRI7NRCTcMP8PoLf96Gah6E5yif7cwNt8Awr26XTqOLj+4RaDEFTdYo6VG3O3+drtgiO79x1coRLZud0xGOgUbO9U+R7oh7jH40fB+U2HTDCDrrmB1Ad51gqxdtRcXwfJu/MzGrKyeGneLLBEhf3Mqs+p18hH1ZHjcrKmLD1b9vgqvPYJROMttv7Ew2m+a1Kv6vjqp7h8cE3baS28ZmA1JEKje1pvhpEEFIH40/lj7bwy7FMZxUXg==' checkor='I6ytkejzv9cbMkMHAv+duA=='>ZkVUQqYFmoFn5h18VG9SFEF3z39+wrcnwItNOZJvdT7CKVbAc1PW44iAWXXhxQJPYIQCitt2ZsPPbAz7yW05OmXnw9PCglsso7b/gsKe4a8Itw9o+JFeI1EiA5pQK5A5yaCNEpXGO8RQi7UqId3gwORmwgGYB730kVD1VTLiIdppn8JgCE3F5unGacZ2ZJW2LGNObP6PcodTBaX/DNNaSwgye7RmTRgpxukHDHhds7Q=</MSG>";
		Param param = new Param(result);
		System.out.println(param);

		String checkor = param.getBody().getString("@checkor");
		String passwds = param.getBody().getString("@passwds");
		String fmessage = param.getCDATA();

		Assert.assertEquals(
				"ZkVUQqYFmoFn5h18VG9SFEF3z39+wrcnwItNOZJvdT7CKVbAc1PW44iAWXXhxQJPYIQCitt2ZsPPbAz7yW05OmXnw9PCglsso7b/gsKe4a8Itw9o+JFeI1EiA5pQK5A5yaCNEpXGO8RQi7UqId3gwORmwgGYB730kVD1VTLiIdppn8JgCE3F5unGacZ2ZJW2LGNObP6PcodTBaX/DNNaSwgye7RmTRgpxukHDHhds7Q=",
				fmessage);

	}

	@Test
	public void testWeixinData() throws Exception {
		String content = "<xml><ToUserName><![CDATA[oMYegv2LAjyFZ32rMAm8wbSWT3-k]]></ToUserName>"
				+ "<FromUserName><![CDATA[gh_29d7d3289efd]]></FromUserName>"
				+ "<CreateTime><![CDATA[1551758240086]]></CreateTime>"
				+ "<FuncFlag><![CDATA[0]]></FuncFlag>"
				+ "<MsgType><![CDATA[text]]></MsgType>"
				+ "<Content><![CDATA[感谢您关注上海福彩网公众号，新用户首次关注可免费领取一注随机双色球彩票。"
				+ "<a href='[图片]http://www.swlc.sh.cn/h5/commcode/html/index.html?page=5'>立即领取>></a>]]></Content>"
				+ "</xml>";

		Param param = new Param(Param.REQ);
		param.getBody().addCDATA(content);

		Assert.assertEquals(content, param.getBody().getCDATA());
	}
	
	@Test
	public void testSpeed() throws Exception {
//		String result = "{\"body\":{\"pagesize\":\"10\",\"istate\":\"1\",\"page\":\"\",\"userid\":\"\",\"dbstype\":\"1\"},\"head\":{\"funcId\":\"90142\",\"dataType\":\"json\",\"appId\":\"swlc.weixin\",\"requestId\":\"15617818454310010332\",\"remoteIp\":\"223.104.210.90\",\"appVersion\":\"4.0\",\"token\":\"d7f52e8c49a64f9dae305ee255fb1076\",\"device\":\"52077efdfd804c7e95cdaae7046103a3\"}}";
//		String result = "<?xml version='1.0' encoding='gb2312'?><MSG errcode='8002' errdesc='未查询到用户绑定第三方' passwds='FZ4NN4xg9aAD/TXMKIhhyTCiNMcsMTSFbHbQ7u/GaTU9wqVup12whSZ5M209bGaAXEtbCa80P5Lf7wSK1GwqdvnqYSHXW/nkQZRI7NRCTcMP8PoLf96Gah6E5yif7cwNt8Awr26XTqOLj+4RaDEFTdYo6VG3O3+drtgiO79x1coRLZud0xGOgUbO9U+R7oh7jH40fB+U2HTDCDrrmB1Ad51gqxdtRcXwfJu/MzGrKyeGneLLBEhf3Mqs+p18hH1ZHjcrKmLD1b9vgqvPYJROMttv7Ew2m+a1Kv6vjqp7h8cE3baS28ZmA1JEKje1pvhpEEFIH40/lj7bwy7FMZxUXg==' checkor='I6ytkejzv9cbMkMHAv+duA=='>ZkVUQqYFmoFn5h18VG9SFEF3z39+wrcnwItNOZJvdT7CKVbAc1PW44iAWXXhxQJPYIQCitt2ZsPPbAz7yW05OmXnw9PCglsso7b/gsKe4a8Itw9o+JFeI1EiA5pQK5A5yaCNEpXGO8RQi7UqId3gwORmwgGYB730kVD1VTLiIdppn8JgCE3F5unGacZ2ZJW2LGNObP6PcodTBaX/DNNaSwgye7RmTRgpxukHDHhds7Q=</MSG>";
		String result = "<xml><ToUserName><![CDATA[oMYegv2LAjyFZ32rMAm8wbSWT3-k]]></ToUserName>"
				+ "<FromUserName><![CDATA[gh_29d7d3289efd]]></FromUserName>"
				+ "<CreateTime><![CDATA[1551758240086]]></CreateTime>"
				+ "<FuncFlag><![CDATA[0]]></FuncFlag>"
				+ "<MsgType><![CDATA[text]]></MsgType>"
				+ "<Content><![CDATA[感谢您关注上海福彩网公众号，新用户首次关注可免费领取一注随机双色球彩票。"
				+ "<a href='[图片]http://www.swlc.sh.cn/h5/commcode/html/index.html?page=5'>立即领取>></a>]]></Content>"
				+ "</xml>";
		
		long startTime = System.currentTimeMillis();
		for (int i = 0; i < 1000; i++) {
			Param param = new Param(result);
			param.toXmlString();
		}
		System.out.println("speed time : " + (System.currentTimeMillis()-startTime));

		Assert.assertTrue(true);
	}
	
	@Test
	public void testSetProperty() throws ParamException{
		Object name = "Tom";
		Param in = new Param(Param.REQ);
		in.getBody().setProperty("@name ", name);
		in.getBody().setProperty(" @password", "123456");
		in.getBody().setProperty("@age", 18);
		
		Assert.assertEquals("Tom", in.getBody().getString("@name ", "Jetty"));
		Assert.assertEquals("123456", in.getBody().getString(" @password", "cat"));
		Assert.assertEquals("cat", in.getBody().getString("@password1", "cat"));
		Assert.assertEquals(18, in.getBody().getInteger("@age", 20));
		Assert.assertEquals(20, in.getBody().getInteger("@age1", 20));
	}

}
