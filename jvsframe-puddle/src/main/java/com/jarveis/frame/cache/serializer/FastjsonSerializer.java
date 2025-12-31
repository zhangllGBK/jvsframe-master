package com.jarveis.frame.cache.serializer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * fastjson序列化工具
 *
 * @author liuguojun
 * @since 2018-08-31
 */
public class FastjsonSerializer implements Serializer {

	public byte[] serialize(Object obj) {
		return JSON.toJSONString(obj, SerializerFeature.WriteClassName)
				.getBytes();
	}

	public Object deserialize(byte[] bytes) {
		return JSON.parse(new String(bytes), Feature.SupportAutoType);
	}

}
