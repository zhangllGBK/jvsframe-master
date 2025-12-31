package com.jarveis.frame.cache.serializer;

import com.jarveis.frame.bean.ReflectionUtils;
import com.jarveis.frame.cache.CacheException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * 序列化工具类
 *
 * @author liuguojun
 * @since 2018-08-31
 */
public class SerializationUtils {

	private static final Logger log = LoggerFactory.getLogger(SerializationUtils.class);

	private static Serializer g_serializer;

	/**
	 * 初始化序列化器
	 * 
	 * @param ser
	 *            serialization method
	 */
	public static void init(String ser) {
		if (ser == null || "".equals(ser.trim()))
			g_serializer = new JavaSerializer();
		else {
			if ("java".equals(ser)) {
				g_serializer = new JavaSerializer();
			} else if ("fst".equals(ser)) {
				g_serializer = new FSTSerializer();
			} else if ("fastjson".equals(ser)) {
				g_serializer = new FastjsonSerializer();
			} else {
				try {
					g_serializer = (Serializer) ReflectionUtils.newInstance(ser);
				} catch (Exception e) {
					throw new CacheException(
							"Cannot initialize Serializer named [" + ser + ']',
							e);
				}
			}
		}
		log.info("Using Serializer -> [" + g_serializer.getClass().getName()
				+ ']');
	}

	/**
	 * 针对不同类型做单独处理
	 * 
	 * @param obj
	 *            待序列化的对象
	 * @return 返回序列化后的字节数组
	 * @throws IOException
	 *             io exception
	 */
	public static byte[] serialize(Object obj) {
		if (obj == null)
			return null;
		return g_serializer.serialize(obj);
	}

	/**
	 * 反序列化
	 * 
	 * @param bytes
	 *            待反序列化的字节数组
	 * @return 序列化后的对象
	 * @throws IOException
	 *             io exception
	 */
	public static Object deserialize(byte[] bytes) {
		if (bytes == null || bytes.length == 0)
			return null;
		return g_serializer.deserialize(bytes);
	}
}
