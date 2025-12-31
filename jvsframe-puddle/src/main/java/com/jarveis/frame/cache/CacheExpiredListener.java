package com.jarveis.frame.cache;

/**
 * 缓存有有效期监听器接口
 *
 * @author liuguojun
 * @since 2018-08-31
 */
public interface CacheExpiredListener {

	public void notifyElementExpired(String region, Object key);

}
