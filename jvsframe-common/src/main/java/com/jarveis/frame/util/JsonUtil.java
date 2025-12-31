package com.jarveis.frame.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * JSON的工具类
 * 
 */
public final class JsonUtil {

	private JsonUtil() {
	}

	/**
	 * 将json转换成对象
	 * 
	 * @param json json字符串
	 * @return 解析后的对象
	 */
	public static Object parse(String json) {
		return JSON.parse(json);
	}

	/**
	 * 将json通过类型转换成对象
	 * 
	 * <pre>
	 * JsonUtil.parse(&quot;{\&quot;uname\&quot;:\&quot;uname\&quot;, \&quot;upwd\&quot;:\&quot;upwd\&quot;}&quot;, User.class);
	 * </pre>
	 * 
	 * @param json
	 *            json字符串
	 * @param clazz
	 *            泛型类型
	 * @return 返回对象
	 */
	public static <T> T parse(String json, Class<T> clazz) {
		return JSON.parseObject(json, clazz);
	}

	/**
	 * 将对象转换成json
	 * 
	 * @param src 数据对象
	 * @return 格式化后的字符串
	 */
	public static String toJson(Object src) {
		return JSON.toJSONString(src);
	}

	/**
	 * 将对象转换成json
	 * 
	 * <pre>
	 * JsonUtil.toJson(user);
	 * </pre>
	 * 
	 * @param src
	 *            对象
	 * @return 返回json字符串
	 */
	public static String toJson(Object src, SerializerFeature writeClassName) {
		return JSON.toJSONString(src, writeClassName);
	}

}