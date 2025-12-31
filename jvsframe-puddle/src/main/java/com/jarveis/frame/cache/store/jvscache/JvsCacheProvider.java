package com.jarveis.frame.cache.store.jvscache;

import com.jarveis.frame.cache.Cache;
import com.jarveis.frame.cache.CacheExpiredListener;
import com.jarveis.frame.cache.CacheProvider;
import com.jarveis.frame.cache.PuddleConfig;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * ehcache缓存的供应商
 * 
 * @author liuguojun
 * @since 2024-03-05
 */
public class JvsCacheProvider implements CacheProvider {

	private static final Logger log = LoggerFactory.getLogger(JvsCacheProvider.class);

	public String name() {
		return "jvscache";
	}

	public Cache buildCache(String regionName, boolean autoCreate, CacheExpiredListener listener) {
		JvsCache cache = JvsCacheManager.getCache(regionName);
		if (cache == null && autoCreate) {
			cache = new JvsCache(regionName);
			JvsCacheManager.putCache(regionName, cache);
			if (log.isDebugEnabled()) {
				log.debug("create cache["+regionName+"] successed.");
			}
		} else {
			cache.setListener(listener);
		}

		return cache;
	}

	public void start() {
		Properties props = PuddleConfig.getJvsCacheConfig();
		String maxSize = getProperty(props, "maxSize", "-1");
		JvsCacheManager.setMaxSize(NumberUtils.toInt(maxSize));
		String disk = getProperty(props, "disk", "");
		JvsCacheManager.setDisk(disk);
        try {
			long ct = System.currentTimeMillis();
            JvsCacheManager.loadBinLog();
			System.out.println("jvscache load time=" + (System.currentTimeMillis() - ct));
        } catch (Exception ex) {
			log.error(ex.getMessage(), ex);
        }
    }

	public void stop() {

	}

	private String getProperty(Properties props, String key, String defaultValue) {
		if (props != null) {
			return props.getProperty(key, defaultValue).trim();
		} else {
			return defaultValue;
		}
	}

}
