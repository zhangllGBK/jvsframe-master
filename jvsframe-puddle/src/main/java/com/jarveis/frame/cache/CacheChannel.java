package com.jarveis.frame.cache;

import java.util.List;

/**
 * 缓存通道接口
 *
 * @author liuguojun
 * @since 2018-08-31
 */
public interface CacheChannel {

	byte LEVEL_1 = 1;
	byte LEVEL_2 = 2;

	/**
	 * 写入缓存
	 * 
	 * @param region 区域（库）
	 * @param key 键
	 * @param value 值
	 */
	public void put(String region, Object key, Object value);

	/**
	 * 写入缓存
	 * 
	 * @param region 区域（库）
	 * @param key 键
	 * @param value 值
	 * @param expireInSec 有效期
	 */
	public void put(String region, Object key, Object value, Integer expireInSec);

	/**
	 * 获取缓存中的数据
	 * 
	 * @param region 区域（库）
	 * @param key 键
	 * @return 缓存对象
	 */
	public CacheObject get(String region, Object key);

	/**
	 * 删除缓存
	 * 
	 * @param region 区域（库）
	 * @param key 键
	 */
	public void remove(String region, Object key);

	/**
	 * 校验key值是否存在
	 * 
	 * @param key
	 * @since 2022-03-28
	 */
	public CacheObject exists(String region, Object key);

	/**
	 * 批量删除缓存
	 * 
	 * @param region 区域（库）
	 * @param keys 键
	 */
	public void removeAll(String region, List keys);

	/**
	 * 清空缓存
	 * 
	 * @param region 区域（库）
	 */
	public void clear(String region);
	
	/**
	 * 关闭通道连接
	 */
	public void close();

	/**
	 * 从左边添加数据到列表
	 *
	 * @param region 区域（库）
	 * @param key 键
	 * @param value 值
	 * @since 2020-09-15
	 */
	public void lpush(String region, Object key, Object value);

	/**
	 * 从右边添加数据到列表
	 *
	 * @param region 区域（库）
	 * @param key 键
	 * @param value 值
	 * @since 2020-09-15
	 */
	public void rpush(String region, Object key, Object value);

	/**
	 * 从列表的左边获取数据
	 *
	 * @param region 区域（库）
	 * @param key 键
	 * @return 缓存对象
	 * @since 2020-09-15
	 */
	public CacheObject lpop(String region, Object key);

	/**
	 * 从列表的右边获取数据
	 *
	 * @param region 区域（库）
	 * @param key 键
	 * @return 缓存对象
	 * @since 2020-09-15
	 */
	public CacheObject rpop(String region, Object key);

	/**
	 * 获取列表长度
	 *
	 * @param region 区域（库）
	 * @param key 键
	 * @return
	 * @since 2020-10-12
	 */
	public CacheObject llen(String region, Object key);

	/**
	 * 从列表的右边获取数据
	 *
	 * @param region 区域（库）
	 * @param key 键
	 * @param count 个数
	 * @return 缓存对象
	 * @since 2020-10-10
	 */
	public CacheObject lrange(String region, Object key, int count);

	/**
	 * 从列表的左边删除指定个数的数据集
	 *
	 * @param region 区域（库）
	 * @param key 键
	 * @param count 个数
	 * @since 2020-10-12
	 */
	public void lrem(String region, Object key, int count);

	/**
	 * 获取hash集合属性值
	 *
	 * @param key
	 * @param field
	 * @return
	 * @since 2020-12-17
	 */
	public CacheObject hget(String region, Object key, String field);

	/**
	 * 设置hash集合的属性值
	 * @param key
	 * @param field
	 * @param value
	 * @since 2020-12-17
	 */
	public void hput(String region, Object key, String field, Object value);

	/**
	 * 删除hash集合的属性及值
	 * @param key
	 * @param field
	 * @since 2020-12-17
	 */
	public void hrem(String region, Object key, String field);

	/**
	 * 校验hash集合的属性值是否存在
	 * 
	 * @param key
	 * @param field
	 * @since 2022-03-28
	 */
	public CacheObject hexists(String region, Object key, String field);
}
