package com.jarveis.frame.cache.store.ehcache;

import com.jarveis.frame.cache.Cache;
import com.jarveis.frame.cache.CacheException;
import com.jarveis.frame.cache.CacheExpiredListener;
import com.jarveis.frame.cache.CacheProvider;
import com.jarveis.frame.util.Resource;
import net.sf.ehcache.CacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * ehcache缓存的供应商
 * 
 * @author liuguojun
 * @since 2018-08-31
 */
public class EhCacheProvider implements CacheProvider {

	private static final Logger log = LoggerFactory.getLogger(EhCacheProvider.class);

	private final static String CONFIG_XML = "/ehcache.xml";

	private CacheManager manager;
	private ConcurrentHashMap<String, EhCache> caches;

	public String name() {
		return "ehcache";
	}

	public Cache buildCache(String regionName, boolean autoCreate, CacheExpiredListener listener) {
		EhCache ehcache = caches.get(regionName);
		if (ehcache == null && autoCreate) {
			try {
				synchronized (caches) {
					ehcache = caches.get(regionName);
					if (ehcache == null) {
						net.sf.ehcache.Cache cache = manager.getCache(regionName);
						if (cache == null) {
							log.warn("Could not find configuration [" + regionName + "]; using defaults.");
							manager.addCache(regionName);
							cache = manager.getCache(regionName);
							if (log.isDebugEnabled()) {
								log.debug("started EHCache region: " + regionName);
							}
						}
						ehcache = new EhCache(cache, listener);
						caches.put(regionName, ehcache);
					}
				}
			} catch (net.sf.ehcache.CacheException e) {
				throw new CacheException(e);
			}
		}
		return ehcache;
	}

	public void start() {
		if (manager != null) {
			log.warn("Attempt to restart an already started EhCacheProvider.");
			return;
		}
		manager = new CacheManager(Resource.getURL(CONFIG_XML));
		caches = new ConcurrentHashMap<String, EhCache>();
	}

	public void stop() {
		if (manager != null) {
			manager.shutdown();
			manager = null;
		}
	}

}
