package com.jarveis.frame.cache.store.none;

import com.jarveis.frame.cache.Cache;
import com.jarveis.frame.cache.CacheExpiredListener;
import com.jarveis.frame.cache.CacheProvider;
import com.jarveis.frame.cache.PuddleConfig;

/**
 * @desc 空缓存的供应商
 * @author liuguojun
 * @date 2018-08-31
 */
public class NullCacheProvider implements CacheProvider {

	private final static NullCache cache = new NullCache();

	@Override
	public String name() {
		return "none";
	}

	@Override
	public Cache buildCache(String regionName, boolean autoCreate, CacheExpiredListener listener) {
		return cache;
	}

	@Override
	public void start() {
	}

	@Override
	public void stop() {
	}

}
