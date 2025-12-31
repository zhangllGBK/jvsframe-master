package com.jarveis.frame.security;

import org.junit.Assert;
import org.junit.Test;

public class CipherTest {

	@Test
	public void testSession() throws Exception {
		// 客户端请求加密
		String[] clientResult = ClientCipher.reqSession("test", "helloWorld");
		Assert.assertEquals("0000", clientResult[0]);
		System.out.println("clientResult[1]=" + clientResult[1]);
		System.out.println("clientResult[2]=" + clientResult[2]);

		// 服务端请求解密
		String[] serverResult = ServerCipher.reqSession("test",
				clientResult[1], clientResult[2]);
		Assert.assertEquals("0000", serverResult[0]);
		System.out.println("serverResult[1]=" + serverResult[1]);

		// 服务端返回加密
		serverResult = ServerCipher.respSession("test", "javaService");
		Assert.assertEquals("0000", clientResult[0]);
		System.out.println("serverResult[1]=" + serverResult[1]);
		System.out.println("serverResult[2]=" + serverResult[2]);

		// 客户端面返回解密
		clientResult = ClientCipher.respSession("test", serverResult[1],
				serverResult[2]);
		Assert.assertEquals("0000", clientResult[0]);
		System.out.println("serverResult[1]=" + clientResult[1]);
	}

	@Test
	public void testService() throws Exception {
		// 客户端请求加密
		String[] clientResult = ClientCipher.reqService("8888", "helloWorld");
		Assert.assertEquals("0000", clientResult[0]);
		System.out.println("clientResult[1]=" + clientResult[1]);
		System.out.println("clientResult[2]=" + clientResult[2]);

		// 服务端请求解密
		String[] serverResult = ServerCipher.reqService("8888",
				clientResult[1], clientResult[2]);
		Assert.assertEquals("0000", serverResult[0]);
		System.out.println("serverResult[1]=" + serverResult[1]);

		// 服务端返回加密
		serverResult = ServerCipher.respService("8888", "javaService");
		Assert.assertEquals("0000", clientResult[0]);
		System.out.println("serverResult[1]=" + serverResult[1]);
		System.out.println("serverResult[2]=" + serverResult[2]);

		// 客户端面返回解密
		clientResult = ClientCipher.respService("8888", serverResult[1],
				serverResult[2]);
		Assert.assertEquals("0000", clientResult[0]);
		System.out.println("serverResult[1]=" + clientResult[1]);
	}
	
	
	@Test
	public void testBytes() throws Exception {
		String str = "hello";
		
		String temp = RSACipher.bytesToHexString(str.getBytes());
		
		byte[] bytes = RSACipher.hexStringToBytes(temp);
		
		Assert.assertEquals(str, new String(bytes));
	}
}
