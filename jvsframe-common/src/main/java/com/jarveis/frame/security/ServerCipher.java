package com.jarveis.frame.security;

import java.util.UUID;

import com.jarveis.frame.util.CharacterUtil;

public class ServerCipher {
	
	/**
	 * 使用私钥进行解密
	 * 
	 * @param fileName
	 * @param contentCipher
	 * @param keyCipher
	 * @return
	 */
	public static String[] reqSession(String fileName, String contentCipher,
			String keyCipher){
		String[] result = new String[2];

		try {
			String key = RSACipher.decryptByPrivate(fileName, keyCipher);
			byte[] plaintext = DESCipher.decrypt(contentCipher, key);
			String contentPlaintext = new String(plaintext);

			result[0] = "0000";
			result[1] = contentPlaintext;
		} catch (Exception ex) {
			result[0] = "9999";
			result[1] = "";
		}

		return result;
	}
	
	/**
	 * 使用私钥进行加密
	 * 
	 * @param fileName
	 * @param content
	 * @return
	 */
	public static String[] respSession(String fileName, String content){
		String[] result = new String[3];

		try {
			String key = UUID.randomUUID().toString().replaceAll("-", "");
			String contentCipher = DESCipher.encrypt(
					content.getBytes(CharacterUtil.UTF8), key);
			String keyCipher = RSACipher.encryptByPrivate(fileName,
					key.getBytes(CharacterUtil.UTF8));
			result[0] = "0000";
			result[1] = contentCipher;
			result[2] = keyCipher;
		} catch (Exception ex) {
			result[0] = "9999";
			result[1] = "";
			result[2] = "";
		}

		return result;
	}
	
	/**
	 * 使用公钥进行解密
	 * 
	 * @param fileName
	 * @param contentCipher
	 * @param keyCipher
	 * @return
	 */
	public static String[] reqService(String fileName, String contentCipher,
			String keyCipher){
		String[] result = new String[2];

		try {
			String key = RSACipher.decryptByPublic(fileName, keyCipher);
			byte[] plaintext = DESCipher.decrypt(contentCipher, key);
			String contentPlaintext = new String(plaintext);

			result[0] = "0000";
			result[1] = contentPlaintext;
		} catch (Exception ex) {
			result[0] = "9999";
			result[1] = "";
		}

		return result;
	}
	
	/**
	 * 使用公钥进行加密
	 * 
	 * @param fileName
	 * @param content
	 * @return
	 */
	public static String[] respService(String fileName, String content){
		String[] result = new String[3];

		try {
			String key = UUID.randomUUID().toString().replaceAll("-", "");
			String contentCipher = DESCipher.encrypt(
					content.getBytes(CharacterUtil.UTF8), key);
			String keyCipher = RSACipher.encryptByPublic(fileName,
					key.getBytes(CharacterUtil.UTF8));
			result[0] = "0000";
			result[1] = contentCipher;
			result[2] = keyCipher;
		} catch (Exception ex) {
			result[0] = "9999";
			result[1] = "";
			result[2] = "";
		}

		return result;
	}
}
