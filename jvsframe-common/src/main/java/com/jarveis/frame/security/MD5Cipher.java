package com.jarveis.frame.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;

/**
 * MD5加密
 * 
 * @author liuguojun
 */
public class MD5Cipher {

	private static final Logger log = LoggerFactory.getLogger(MD5Cipher.class);

	// 十六进制下数字到字符的映射数组
	private final static String[] hexDigits = { "0", "1", "2", "3", "4", "5",
			"6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

	private MD5Cipher() {
	}
	
	/**
	 * 对指定的参数进行加密
	 * 
	 * @param str
	 *            需要加密的字符串
	 */
	public static String encrypt(String str) {
		return encodeByMD5(str);
	}

	/**
	 * 验证加密字符串是否相同
	 * 
	 * @param encryptStr
	 *            加密后的字符串
	 * @param str
	 *            加密前的字符串
	 * @return boolean
	 */
	public static boolean validate(String encryptStr, String str) {
		if (encryptStr.equals(encodeByMD5(str))) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 对字符串进行MD5加密
	 * 
	 * @param str
	 *            需要加密的字符串
	 */
	private static String encodeByMD5(String str) {
		if (str != null) {
			try {
				// 创建具有指定算法名称的信息摘要
				MessageDigest md = MessageDigest.getInstance("MD5");
				// 使用指定的字节数组对摘要进行最后更新，然后完成摘要计算
				byte[] results = md.digest(str.getBytes());
				// 将得到的字节数组变成字符串返回
				String resultString = byteArrayToHexString(results);
				return resultString.toUpperCase();
			} catch (Exception ex) {
				log.error(ex.getMessage(), ex);
			}
		}
		return null;
	}

	/**
	 * 转换字节数组为十六进制字符串
	 * 
	 * @param b
	 *            字节数组
	 * @return 十六进制字符串
	 */
	private static String byteArrayToHexString(byte[] b) {
		StringBuffer resultSb = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			resultSb.append(byteToHexString(b[i]));
		}
		return resultSb.toString();
	}

	/**
	 * 将一个字节转化成十六进制形式的字符串
	 */
	private static String byteToHexString(byte b) {
		int n = b;
		if (n < 0)
			n = 256 + n;
		int d1 = n / 16;
		int d2 = n % 16;
		return hexDigits[d1] + hexDigits[d2];
	}
}
