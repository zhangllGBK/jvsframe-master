package com.jarveis.frame.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * BASE64加密
 * 
 * @author liuguojun
 */
public class BASE64Cipher {

	private static final Logger log = LoggerFactory.getLogger(BASE64Cipher.class);

	private static final char[] CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
			.toCharArray();
	private static final int[] INV = new int[256];

	static {
		Arrays.fill(INV, -1);
		for (int i = 0, iS = CHARS.length; i < iS; i++) {
			INV[CHARS[i]] = i;
		}
		INV['='] = 0;
	}
	
	private BASE64Cipher() {
	}

	/**
	 * 加密
	 * 
	 * @param str
	 * @return String
	 * @throws Exception
	 */
	public static String encrypt(String str) {
		try {
			byte[] bytes = str.getBytes();
			return new String(encode(bytes, false));
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			return null;
		}
	}

	/**
	 * 解密
	 * 
	 * @param str
	 * @return String
	 * @throws Exception
	 */
	public static String decrypt(String str) {
		try {
			byte[] bytes = str.getBytes();
			bytes = decode(bytes);
			return new String(bytes);
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			return null;
		}
	}

	/**
	 * Encodes a raw byte array into a BASE64 <code>byte[]</code>.
	 */
	public static byte[] encode(byte[] arr, boolean lineSep) {
		int len = arr != null ? arr.length : 0;
		if (len == 0) {
			return new byte[0];
		}

		int evenlen = (len / 3) * 3;
		int cnt = ((len - 1) / 3 + 1) << 2;
		int destlen = cnt + (lineSep ? (cnt - 1) / 76 << 1 : 0);
		byte[] dest = new byte[destlen];

		for (int s = 0, d = 0, cc = 0; s < evenlen;) {
			int i = (arr[s++] & 0xff) << 16 | (arr[s++] & 0xff) << 8
					| (arr[s++] & 0xff);

			dest[d++] = (byte) CHARS[(i >>> 18) & 0x3f];
			dest[d++] = (byte) CHARS[(i >>> 12) & 0x3f];
			dest[d++] = (byte) CHARS[(i >>> 6) & 0x3f];
			dest[d++] = (byte) CHARS[i & 0x3f];

			if (lineSep && ++cc == 19 && d < destlen - 2) {
				dest[d++] = '\r';
				dest[d++] = '\n';
				cc = 0;
			}
		}

		int left = len - evenlen;
		if (left > 0) {
			int i = ((arr[evenlen] & 0xff) << 10)
					| (left == 2 ? ((arr[len - 1] & 0xff) << 2) : 0);

			dest[destlen - 4] = (byte) CHARS[i >> 12];
			dest[destlen - 3] = (byte) CHARS[(i >>> 6) & 0x3f];
			dest[destlen - 2] = left == 2 ? (byte) CHARS[i & 0x3f] : (byte) '=';
			dest[destlen - 1] = '=';
		}
		return dest;
	}

	/**
	 * Decodes a BASE64 encoded byte array.
	 */
	public static byte[] decode(byte[] arr) {
		int length = arr.length;
		if (length == 0) {
			return new byte[0];
		}

		int sndx = 0, endx = length - 1;
		int pad = arr[endx] == '=' ? (arr[endx - 1] == '=' ? 2 : 1) : 0;
		int cnt = endx - sndx + 1;
		int sepCnt = length > 76 ? (arr[76] == '\r' ? cnt / 78 : 0) << 1 : 0;
		int len = ((cnt - sepCnt) * 6 >> 3) - pad;
		byte[] dest = new byte[len];

		int d = 0;
		for (int cc = 0, eLen = (len / 3) * 3; d < eLen;) {
			int i = INV[arr[sndx++]] << 18 | INV[arr[sndx++]] << 12
					| INV[arr[sndx++]] << 6 | INV[arr[sndx++]];

			dest[d++] = (byte) (i >> 16);
			dest[d++] = (byte) (i >> 8);
			dest[d++] = (byte) i;

			if (sepCnt > 0 && ++cc == 19) {
				sndx += 2;
				cc = 0;
			}
		}

		if (d < len) {
			int i = 0;
			for (int j = 0; sndx <= endx - pad; j++) {
				i |= INV[arr[sndx++]] << (18 - j * 6);
			}
			for (int r = 16; d < len; r -= 8) {
				dest[d++] = (byte) (i >> r);
			}
		}

		return dest;
	}



	

	public static void main(String[] args) {
		try {
			String content = "Hello World!";
			System.out.println("content=" + content);
			content = BASE64Cipher.encrypt(content);
			System.out.println("encode=" + content);
			content = BASE64Cipher.decrypt(content);
			System.out.println("decode=" + content);
		} catch (Exception ex) {

		}
	}
}
