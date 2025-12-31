package com.jarveis.frame.cache.serializer;

/**
 * @desc 序列化接口
 * @author liuguojun
 * @date 2018-08-31
 */
public interface Serializer {

	/**
	 * Serialize Object
	 * 
	 * @param obj
	 * @return
	 */
	public byte[] serialize(Object obj);

	/**
	 * Deserialize to object
	 * 
	 * @param bytes
	 * @return
	 */
	public Object deserialize(byte[] bytes);
}
