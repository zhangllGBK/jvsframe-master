package com.jarveis.frame.cache;

/**
 * 缓存供应商接口
 *
 * @author liuguojun
 * @since 2018-08-31
 */
public interface CacheProvider {

	/**
	 * 供应商的名字
	 * 
	 * @return
	 */
	public String name();

	/**
	 * 创建缓存库
	 * 
	 * @param regionName
	 * @param autoCreate
	 * @param listener
	 * @return
	 */
	public Cache buildCache(String regionName, boolean autoCreate, CacheExpiredListener listener);

	/**
	 * 开启缓存
	 */
	public void start();

	/**
	 * 关闭缓存
	 */
	public void stop();

}
