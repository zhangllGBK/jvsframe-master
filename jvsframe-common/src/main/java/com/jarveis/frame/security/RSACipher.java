package com.jarveis.frame.security;

import com.jarveis.frame.util.Resource;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RSACipher {

	private static final Logger log = LoggerFactory.getLogger(RSACipher.class);

	/** 算法名称 */
	private static final String ALGORITHM = "RSA";
	/** 默认密钥大小 */
	private static final int KEY_SIZE = 1024;
	/** 密钥对生成器 */
	private static KeyPairGenerator keyPairGenerator = null;

	private static KeyFactory keyFactory = null;
	/** 缓存的密钥对 */
	private static KeyPair keyPair = null;

	/** 公钥缓存 */
	private static Map<String, byte[]> pubKeyStore = new HashMap<String, byte[]>();
	/** 私钥缓存 */
	private static Map<String, byte[]> priKeyStore = new HashMap<String, byte[]>();

	/** 初始化密钥工厂 */
	static {
		try {
			keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
			keyFactory = KeyFactory.getInstance(ALGORITHM);
		} catch (NoSuchAlgorithmException e) {
			log.error(e.getMessage(), e);
		}
	}

	private RSACipher() {
	}

	/**
	 * 生成密钥对 将密钥保存到#{fileName}.pub#和#{fileName}.pri#文件中
	 * 
	 * @param fileName
	 *            公钥和私钥的文件名称
	 */
	public static synchronized void generateKeyPair(String fileName) {
		try {
			keyPairGenerator.initialize(KEY_SIZE, new SecureRandom(UUID
					.randomUUID().toString().replaceAll("-", "").getBytes()));
			keyPair = keyPairGenerator.generateKeyPair();
		} catch (InvalidParameterException e) {
			log.error("KeyPairGenerator does not support a key length of "
					+ KEY_SIZE + ".", e);
		} catch (NullPointerException e) {
			log.error(
					"key_pair_gen is null,can not generate KeyPairGenerator instance.",
					e);
		}
		RSAPublicKey rsaPublicKey = (RSAPublicKey) keyPair.getPublic();
		RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) keyPair.getPrivate();

		storeKey(rsaPublicKey.getEncoded(), fileName + ".pub");
		storeKey(rsaPrivateKey.getEncoded(), fileName + ".pri");
	}

	/**
	 * 获取文件的路径
	 * 
	 * @param fileName
	 *            　密钥文件
	 * @return　path
	 */
	private static String getFilePath(String fileName) {
		// 存放密钥的绝对地址
		String path = null;
		try {
			String proHome = System.getProperty("pro.home");
			if (StringUtils.isEmpty(proHome)) {
				throw new Exception("pro.home not found");
			}
			File file = new File(proHome + "/conf/" + fileName);
			if (!file.exists()) {
				file.createNewFile();
				path = file.getAbsolutePath();
			}
		} catch (Exception e) {
			// 如果不存#fileName#就创建
			log.warn("getFilePath()# " + e.getMessage());
			String classPath = Resource.getClasspath();
			String prefix = classPath.substring(classPath.indexOf(":") + 1);
			String suffix = fileName;
			File file = new File(prefix + suffix);
			try {
				file.createNewFile();
				path = file.getAbsolutePath();
			} catch (IOException e1) {
				log.error(fileName + " create fail.", e1);
			}
		}

		return path;
	}

	/**
	 * 将指定的密钥字符串保存到文件中,如果找不到文件，就创建
	 * 
	 * @param key
	 *            密钥（值）
	 * @param fileName
	 *            目标文件名
	 */
	private static void storeKey(byte[] key, String fileName) {
		try {
			// 存放密钥的绝对地址
			String path = getFilePath(fileName);
			OutputStream out = new FileOutputStream(path);
			IOUtils.write(key, out);
		} catch (FileNotFoundException e) {
			log.error("ModulusAndExponent.properties is not found.", e);
		} catch (IOException e) {
			log.error("OutputStream output failed.", e);
		}
	}

	/**
	 * 获取密钥字符串
	 * 
	 * @param fileName
	 *            密钥所在文件
	 * @return Base64编码的密钥字符串
	 */
	private static byte[] getKey(String fileName) {
		byte[] keys = null;
		try {
			// 存放密钥的绝对地址
			String path = getFilePath(fileName);
			keys = IOUtils.toByteArray(new FileInputStream(path));
			if (keys ==null || keys.length < 1) {
				generateKeyPair(fileName.substring(0, fileName.lastIndexOf('.')));
				keys = IOUtils.toByteArray(new FileInputStream(path));
			}
		} catch (IOException e) {
			log.error("getKey()#" + e.getMessage(), e);
		}
		return keys;
	}

	/**
	 * 从文件获取RSA公钥
	 * 
	 * @param fileName
	 *            公钥文件名称
	 * @return RSA公钥
	 */
	public static RSAPublicKey getPublicKey(String fileName) {
		try {
			byte[] keyBytes = pubKeyStore.get(fileName);
			if (keyBytes == null) {
				keyBytes = getKey(fileName + ".pub");
				pubKeyStore.put(fileName, keyBytes);
			}
			X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(
					keyBytes);
			return (RSAPublicKey) keyFactory.generatePublic(x509EncodedKeySpec);
		} catch (InvalidKeySpecException e) {
			log.error("getPublicKey()#" + e.getMessage(), e);
		}
		return null;
	}

	/**
	 * 从文件获取RSA私钥
	 * 
	 * @param fileName
	 *            私钥文件名称
	 * @return RSA私钥
	 */
	public static RSAPrivateKey getPrivateKey(String fileName) {
		try {
			byte[] keyBytes = priKeyStore.get(fileName);
			if (keyBytes == null) {
				keyBytes = getKey(fileName + ".pri");
				priKeyStore.put(fileName, keyBytes);
			}
			PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(
					keyBytes);
			return (RSAPrivateKey) keyFactory
					.generatePrivate(pkcs8EncodedKeySpec);
		} catch (InvalidKeySpecException e) {
			log.error("getPrivateKey()#" + e.getMessage(), e);
		}
		return null;
	}

	/**
	 * RSA公钥加密
	 * 
	 * @param fileName
	 *            密钥文件名称
	 * @param content
	 *            等待加密的数据
	 * @return 加密后的密文(16进制的字符串)
	 */
	public static String encryptByPublic(String fileName, byte[] content) {
		PublicKey publicKey = getPublicKey(fileName);
		try {
			Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			// 该密钥能够加密的最大字节长度
			int splitLength = ((RSAPublicKey) publicKey).getModulus()
					.bitLength() / 8 - 11;
			byte[][] arrays = splitBytes(content, splitLength);
			StringBuffer stringBuffer = new StringBuffer();
			for (byte[] array : arrays) {
				stringBuffer.append(bytesToHexString(cipher.doFinal(array)));
			}
			return stringBuffer.toString();
		} catch (NoSuchAlgorithmException e) {
			log.error("encrypt()#NoSuchAlgorithmException", e);
		} catch (NoSuchPaddingException e) {
			log.error("encrypt()#NoSuchPaddingException", e);
		} catch (InvalidKeyException e) {
			log.error("encrypt()#InvalidKeyException", e);
		} catch (BadPaddingException e) {
			log.error("encrypt()#BadPaddingException", e);
		} catch (IllegalBlockSizeException e) {
			log.error("encrypt()#IllegalBlockSizeException", e);
		}
		return null;
	}

	/**
	 * RSA私钥解密
	 * 
	 * @param fileName
	 *            密钥文件名称
	 * @param content
	 *            等待解密的数据
	 * @return 解密后的明文
	 */
	public static String decryptByPrivate(String fileName, String content) {
		PrivateKey privateKey = getPrivateKey(fileName);
		try {
			Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			// 该密钥能够加密的最大字节长度
			int splitLength = ((RSAPrivateKey) privateKey).getModulus()
					.bitLength() / 8;
			byte[] contentBytes = hexStringToBytes(content);
			byte[][] arrays = splitBytes(contentBytes, splitLength);
			StringBuffer stringBuffer = new StringBuffer();
			for (byte[] array : arrays) {
				stringBuffer.append(new String(cipher.doFinal(array)));
			}
			return stringBuffer.toString();
		} catch (NoSuchAlgorithmException e) {
			log.error("encrypt()#NoSuchAlgorithmException", e);
		} catch (NoSuchPaddingException e) {
			log.error("encrypt()#NoSuchPaddingException", e);
		} catch (InvalidKeyException e) {
			log.error("encrypt()#InvalidKeyException", e);
		} catch (BadPaddingException e) {
			log.error("encrypt()#BadPaddingException", e);
		} catch (IllegalBlockSizeException e) {
			log.error("encrypt()#IllegalBlockSizeException", e);
		}
		return null;
	}

	/**
	 * RSA私钥加密
	 * 
	 * @param fileName
	 *            密钥文件名称
	 * @param content
	 *            等待加密的数据
	 * @return 加密后的密文(16进制的字符串)
	 */
	public static String encryptByPrivate(String fileName, byte[] content) {
		PrivateKey privateKey = getPrivateKey(fileName);
		try {
			Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
			cipher.init(Cipher.ENCRYPT_MODE, privateKey);
			// 该密钥能够加密的最大字节长度
			int splitLength = ((RSAPrivateKey) privateKey).getModulus()
					.bitLength() / 8 - 11;
			byte[][] arrays = splitBytes(content, splitLength);
			StringBuffer stringBuffer = new StringBuffer();
			for (byte[] array : arrays) {
				stringBuffer.append(bytesToHexString(cipher.doFinal(array)));
			}
			return stringBuffer.toString();
		} catch (NoSuchAlgorithmException e) {
			log.error("encrypt()#NoSuchAlgorithmException", e);
		} catch (NoSuchPaddingException e) {
			log.error("encrypt()#NoSuchPaddingException", e);
		} catch (InvalidKeyException e) {
			log.error("encrypt()#InvalidKeyException", e);
		} catch (BadPaddingException e) {
			log.error("encrypt()#BadPaddingException", e);
		} catch (IllegalBlockSizeException e) {
			log.error("encrypt()#IllegalBlockSizeException", e);
		}
		return null;
	}

	/**
	 * RSA公钥解密
	 * 
	 * @param fileName
	 *            密钥文件名称
	 * @param content
	 *            等待解密的数据
	 * @return 解密后的明文
	 */
	public static String decryptByPublic(String fileName, String content) {
		PublicKey publicKey = getPublicKey(fileName);
		try {
			Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
			cipher.init(Cipher.DECRYPT_MODE, publicKey);
			// 该密钥能够加密的最大字节长度
			int splitLength = ((RSAPublicKey) publicKey).getModulus()
					.bitLength() / 8;
			byte[] contentBytes = hexStringToBytes(content);
			byte[][] arrays = splitBytes(contentBytes, splitLength);
			StringBuffer stringBuffer = new StringBuffer();
			for (byte[] array : arrays) {
				stringBuffer.append(new String(cipher.doFinal(array)));
			}
			return stringBuffer.toString();
		} catch (NoSuchAlgorithmException e) {
			log.error("encrypt()#NoSuchAlgorithmException", e);
		} catch (NoSuchPaddingException e) {
			log.error("encrypt()#NoSuchPaddingException", e);
		} catch (InvalidKeyException e) {
			log.error("encrypt()#InvalidKeyException", e);
		} catch (BadPaddingException e) {
			log.error("encrypt()#BadPaddingException", e);
		} catch (IllegalBlockSizeException e) {
			log.error("encrypt()#IllegalBlockSizeException", e);
		}
		return null;
	}

	/**
	 * 根据限定的每组字节长度，将字节数组分组
	 * 
	 * @param bytes
	 *            等待分组的字节组
	 * @param splitLength
	 *            每组长度
	 * @return 分组后的字节组
	 */
	public static byte[][] splitBytes(byte[] bytes, int splitLength) {
		// bytes与splitLength的余数
		int remainder = bytes.length % splitLength;
		// 数据拆分后的组数，余数不为0时加1
		int quotient = remainder != 0 ? bytes.length / splitLength + 1
				: bytes.length / splitLength;
		byte[][] arrays = new byte[quotient][];
		byte[] array = null;
		for (int i = 0; i < quotient; i++) {
			// 如果是最后一组（quotient-1）,同时余数不等于0，就将最后一组设置为remainder的长度
			if (i == quotient - 1 && remainder != 0) {
				array = new byte[remainder];
				System.arraycopy(bytes, i * splitLength, array, 0, remainder);
			} else {
				array = new byte[splitLength];
				System.arraycopy(bytes, i * splitLength, array, 0, splitLength);
			}
			arrays[i] = array;
		}
		return arrays;
	}

	/**
	 * 将字节数组转换成16进制字符串
	 * 
	 * @param bytes
	 *            即将转换的数据
	 * @return 16进制字符串
	 */
	public static String bytesToHexString(byte[] bytes) {
		StringBuffer sb = new StringBuffer(bytes.length);
		String temp = null;
		for (int i = 0; i < bytes.length; i++) {
			temp = Integer.toHexString(0xFF & bytes[i]);
			if (temp.length() < 2) {
				sb.append(0);
			}
			sb.append(temp);
		}
		return sb.toString();
	}

	/**
	 * 将16进制字符串转换成字节数组
	 * 
	 * @param hex
	 *            16进制字符串
	 * @return byte[]
	 */
	public static byte[] hexStringToBytes(String hex) {
		int len = (hex.length() / 2);
		hex = hex.toUpperCase();
		byte[] result = new byte[len];
		char[] chars = hex.toCharArray();
		for (int i = 0; i < len; i++) {
			int pos = i * 2;
			result[i] = (byte) (toByte(chars[pos]) << 4 | toByte(chars[pos + 1]));
		}
		return result;
	}

	/**
	 * 将char转换为byte
	 * 
	 * @param c
	 *            char
	 * @return byte
	 */
	private static byte toByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}

	public static void main(String[] args) {
		String s = UUID.randomUUID().toString().replaceAll("-", "");
		String c1 = RSACipher.encryptByPublic("test", s.getBytes());
		String m1 = RSACipher.decryptByPrivate("test", c1);
		String c2 = RSACipher.encryptByPrivate("test", s.getBytes());
		String m2 = RSACipher.decryptByPublic("test", c2);
		System.out.println(c1);
		System.out.println(m1);
		System.out.println(c2);
		System.out.println(m2);
	}

}
