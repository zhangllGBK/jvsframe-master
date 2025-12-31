package com.jarveis.frame.security;

import com.jarveis.frame.util.CharacterUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * 加密类
 * 
 * @author liuguojun
 */
public class DESCipher {

	private static final Logger log = LoggerFactory.getLogger(DESCipher.class);

	// 定义加密算法，有DES、DESede(即3DES)、Blowfish
	private static final String Algorithm = "DESede";

	private DESCipher() {
	}

	/**
	 * 加密方法
	 * 
	 * @param src
	 *            源数据的字节数组
	 * @return
	 */
	public static String encrypt(byte[] src, String secretKey) {
		try {
			SecretKey deskey = generateKey(secretKey);
			Cipher c1 = Cipher.getInstance(Algorithm); // 实例化负责加密/解密的Cipher工具类
			c1.init(Cipher.ENCRYPT_MODE, deskey); // 初始化为加密模式

			byte[] buf = c1.doFinal(src);
			return RSACipher.bytesToHexString(buf);
		} catch (java.security.NoSuchAlgorithmException e1) {
			log.error(e1.getMessage(), e1);
		} catch (javax.crypto.NoSuchPaddingException e2) {
			log.error(e2.getMessage(), e2);
		} catch (java.lang.Exception e3) {
			log.error(e3.getMessage(), e3);
		}
		return null;
	}

	/**
	 * 解密函数
	 * 
	 * @param src
	 *            密文的字节数组
	 * @return
	 */
	public static byte[] decrypt(String src, String secretKey) {
		try {
			SecretKey deskey = generateKey(secretKey);
			Cipher c1 = Cipher.getInstance(Algorithm);
			c1.init(Cipher.DECRYPT_MODE, deskey);// 初始化为解密模式

			byte[] buf = RSACipher.hexStringToBytes(src);
			return c1.doFinal(buf);
		} catch (java.security.NoSuchAlgorithmException e1) {
			log.error(e1.getMessage(), e1);
		} catch (javax.crypto.NoSuchPaddingException e2) {
			log.error(e2.getMessage(), e2);
		} catch (java.lang.Exception e3) {
			log.error(e3.getMessage(), e3);
		}
		return null;
	}

	/**
	 * 获得密钥
	 * 
	 * @param secretKey
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 * @throws InvalidKeySpecException
	 */
	private static SecretKey generateKey(String secretKey) throws Exception {
		return new SecretKeySpec(build3DesKey(secretKey), Algorithm); // 生成密钥
	}

	/**
	 * 根据字符串生成密钥字节数组
	 * 
	 * @param keyStr
	 *            密钥字符串
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private static byte[] build3DesKey(String keyStr)
			throws UnsupportedEncodingException {
		byte[] key = new byte[24]; // 声明一个24位的字节数组，默认里面都是0
		byte[] temp = keyStr.getBytes(CharacterUtil.UTF8); // 将字符串转成字节数组

		/*
		 * 执行数组拷贝 System.arraycopy(源数组，从源数组哪里开始拷贝，目标数组，拷贝多少位)
		 */
		if (key.length > temp.length) {
			// 如果temp不够24位，则拷贝temp数组整个长度的内容到key数组中
			System.arraycopy(temp, 0, key, 0, temp.length);
		} else {
			// 如果temp大于24位，则拷贝temp数组24个长度的内容到key数组中
			System.arraycopy(temp, 0, key, 0, key.length);
		}
		return key;
	}

	public static void main(String[] args) throws Exception {
		String secretKey = "1qaz2wsx";

		StringBuffer text = new StringBuffer();
		text.append("{name:'zhangsan', idcard:'100000199912120001'}");

		String tar = DESCipher.encrypt(text.toString().getBytes(), secretKey);
		System.out.println(tar);

		byte[] src = DESCipher.decrypt(tar, secretKey);
		System.out.println("decrypt=" + new String(src));
	}
}
