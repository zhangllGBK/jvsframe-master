package com.jarveis.frame.cache;

import java.util.List;

/**
 * Cache interface
 * 
 * @author liuguojun
 */
public interface Cache {

	/**
	 * 设置缓存对象
	 * 
	 * @param key 键
	 * @param value 值
	 */
	public void put(Object key, Object value);

	/**
	 * 设置缓存对象，有效期的时间单位是秒
	 * 
	 * @param key 键
	 * @param value 值
	 * @param expireInSec 有效期(秒)
	 */
	public void put(Object key, Object value, Integer expireInSec);

	/**
	 * 获取缓存对象
	 * 
	 * @param key 键
	 * @return
	 */
	public Object get(Object key);

	/**
	 * 删除缓存对象
	 * 
	 * @param key 键
	 */
	public void remove(Object key);

	/**
	 * 校验key值是否存在
	 * 
	 * @param key
	 * @return
	 * @since 2022-03-28
	 */
	public Object exists(Object key);


	/**
	 * 删除缓存对象
	 * 
	 * @param keys 键
	 */
	public void removeAll(List keys);

	/**
	 * 清除缓存
	 */
	public void clear();

	/**
	 * Clean up
	 */
	public void destroy();

	/**
	 * 从左边添加数据到列表
	 *
	 * @param key 键
	 * @param value 值
	 * @since 2020-09-15
	 */
	public void lpush(Object key, Object value);

	/**
	 * 从右边添加数据到列表
	 *
	 * @param key 键
	 * @param value 值
	 * @since 2020-09-15
	 */
	public void rpush(Object key, Object value);

	/**
	 * 从列表的左边获取数据
	 *
	 * @param key 键
	 * @return
	 * @since 2020-09-15
	 */
	public Object lpop(Object key);

	/**
	 * 从列表的右边获取数据
	 *
	 * @param key 键
	 * @return
	 * @since 2020-09-15
	 */
	public Object rpop(Object key);

	/**
	 * 获取列表长度
	 *
	 * @param key 键
	 * @return
	 * @since 2020-10-12
	 */
	public Object llen(Object key);

	/**
	 * 从列表的左边获取指定个数的数据集
	 *
	 * @param key 键
	 * @param count 个数
	 * @return
	 * @since 2020-10-10
	 */
	public Object lrange(Object key, int count);

	/**
	 * 从列表的左边删除指定个数的数据
	 *
	 * @param key 键
	 * @param count 个数
	 * @since 2020-10-12
	 */
	public void lrem(Object key, int count);

	/**
	 * 获取hash集合属性值
	 *
	 * @param key
	 * @param field
	 * @return
	 * @since 2020-12-17
	 */
	public Object hget(Object key, String field);

	/**
	 * 设置hash集合的属性值
	 * @param key
	 * @param filed
	 * @param value
	 * @since 2020-12-17
	 */
	public void hput(Object key, String filed, Object value);

	/**
	 * 删除hash集合的属性及值
	 * @param key
	 * @param filed
	 * @since 2020-12-17
	 */
	public void hrem(Object key, String filed);

	/**
	 * 校验hash集合的属性值是否存在
	 * 
	 * @param key
	 * @param field
	 * @return
	 * @since 2022-03-28
	 */
	public Object hexists(Object key, String field);
}
