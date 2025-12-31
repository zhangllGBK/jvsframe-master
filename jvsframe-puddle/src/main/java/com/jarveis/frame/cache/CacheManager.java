package com.jarveis.frame.cache;

import com.jarveis.frame.cache.serializer.SerializationUtils;
import com.jarveis.frame.cache.store.ehcache.EhCacheProvider;
import com.jarveis.frame.cache.store.jvscache.JvsCacheProvider;
import com.jarveis.frame.cache.store.none.NullCacheProvider;
import com.jarveis.frame.cache.store.redis.RedisCacheProvider;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Properties;

/**
 * 缓存管理类
 * 
 * @author liuguojun
 * @since 2018-08-31
 */
public final class CacheManager {

	private static final Logger log = LoggerFactory.getLogger(CacheManager.class);

	private static CacheProvider l1_provider;
	private static CacheProvider l2_provider;
	private static CacheExpiredListener listener;

	/**
	 * 初始化L1与L2的缓存操作对象
	 *
	 * @param listener
	 */
	public static void initCacheProvider(CacheExpiredListener listener) {

		CacheManager.listener = listener;

		Properties cacheConfig = PuddleConfig.getCacheConfig();

		// 设置L1处理类
		CacheManager.l1_provider = getProviderInstance(cacheConfig.getProperty("l1_provider"));
		CacheManager.l1_provider.start();
		log.info("Using L1 CacheProvider : " + l1_provider.getClass().getName());

		if (NumberUtils.toInt(cacheConfig.getProperty("level"), 1) == 2) {
			// 设置L2处理类
			CacheManager.l2_provider = getProviderInstance(cacheConfig.getProperty("l2_provider"));
			CacheManager.l2_provider.start();
			log.info("Using L2 CacheProvider : " + l2_provider.getClass().getName());
		}

		SerializationUtils.init(cacheConfig.getProperty("serialization"));
	}

	/**
	 * 实例化缓存操作对象
	 *
	 * @param value
	 * @return
	 */
	private final static CacheProvider getProviderInstance(String value) {
		if ("ehcache".equalsIgnoreCase(value)) {
			return new EhCacheProvider();
		} else if ("redis".equalsIgnoreCase(value)) {
			return new RedisCacheProvider();
		} else if ("jvscache".equalsIgnoreCase(value)) {
			return new JvsCacheProvider();
		} else {
			return new NullCacheProvider();
		}
	}

	/**
	 * 获取缓存仓库对象
	 *
	 * @param level 缓存级别
	 * @param cache_name 仓库名称
	 * @param autoCreate 不存在时是否自动创建
	 * @return
	 */
	private final static Cache _GetCache(int level, String cache_name, boolean autoCreate) {
		Cache cache = null;
		if (level == 1) {
			cache = l1_provider.buildCache(cache_name, autoCreate, listener);
		} else {
			if (l2_provider != null) {
				cache = l2_provider.buildCache(cache_name, autoCreate, listener);
			}
		}
		return cache;
	}

	/**
	 * 停止缓存操作
	 *
	 * @param level
	 */
	public final static void shutdown(int level) {
		if (level == 1) {
			l1_provider.stop();
		} else {
			if (l2_provider != null) {
				l2_provider.stop();
			}
		}
	}

	/**
	 * 获取缓存中的数据
	 * 
	 * @param level 缓存等级
	 * @param name 仓库
	 * @param key 键
	 * @return 值
	 */
	public final static Object get(int level, String name, Object key) {
		if (name != null && key != null) {
			Cache cache = _GetCache(level, name, false);
			if (cache != null) {
				return cache.get(key);
			}
		}
		return null;
	}

	/**
	 * 写入缓存
	 * 
	 * @param level 缓存级别
	 * @param name 区域(库)
	 * @param key 键
	 * @param value 值
	 */
	public final static void put(int level, String name, Object key, Object value) {
		if (name != null && key != null && value != null) {
			Cache cache = _GetCache(level, name, true);
			if (cache != null) {
				cache.put(key, value);
			}
		}
	}

	/**
	 * 写入缓存
	 * 
	 * @param level 缓存级别
	 * @param name 区域(库)
	 * @param key 键
	 * @param value 值
	 * @param expireInSec 有效时间
	 */
	public final static void put(int level, String name, Object key, Object value, Integer expireInSec) {
		if (name != null && key != null && value != null) {
			Cache cache = _GetCache(level, name, true);
			if (cache != null) {
				cache.put(key, value, expireInSec);
			}
		}
	}

	/**
	 * 清除缓存中的某个数据
	 * 
	 * @param level 缓存级别
	 * @param name 区域(库)
	 * @param key 键
	 */
	public final static void remove(int level, String name, Object key) {
		if (name != null && key != null) {
			Cache cache = _GetCache(level, name, false);
			if (cache != null) {
				cache.remove(key);
			}
		}
	}

	/**
	 *校验缓存中的某个数据
	 *
	 * @param level 缓存级别
	 * @param name 区域(库)
	 * @param key 键
	 * @since 2022-03-28
	 */
	public final static Object exists(int level, String name, Object key) {
		if (name != null && key != null) {
			Cache cache = _GetCache(level, name, false);
			if (cache != null) {
				return cache.exists(key);
			}
		}
		return null;
	}

	/**
	 * 批量删除缓存中的一些数据
	 * 
	 * @param level 缓存级别
	 * @param name 区域(库)
	 * @param keys 键
	 */
	public final static void removeAll(int level, String name, List keys) {
		if (name != null && keys != null && keys.size() > 0) {
			Cache cache = _GetCache(level, name, false);
			if (cache != null) {
				cache.removeAll(keys);
			}
		}
	}

	/**
	 * Clear the cache
	 *
	 * @param level 缓存级别
	 * @param name 区域(库)
	 */
	public final static void clear(int level, String name) throws CacheException {
		Cache cache = _GetCache(level, name, false);
		if (cache != null) {
			cache.clear();
		}
	}

    /**
     * 从左边添加数据到列表
     *
     * @param level 缓存级别
	 * @param name 区域(库)
	 * @param key 键
     * @param value 值
     * @since 2020-09-15
     */
    public static void lpush(int level, String name, Object key, Object value) {
		if (name != null && key != null && value != null) {
			Cache cache = _GetCache(level, name, true);
			if (cache != null) {
				cache.lpush(key, value);
			}
		}
    }

    /**
     * 从右边添加数据到列表
     *
     * @param level 缓存级别
	 * @param name 区域(库)
	 * @param key 键
     * @param value 值
     * @since 2020-09-15
     */
    public static void rpush(int level, String name, Object key, Object value) {
		if (name != null && key != null && value != null) {
			Cache cache = _GetCache(level, name, true);
			if (cache != null) {
				cache.rpush(key, value);
			}
		}
	}

    /**
     * 从列表的左边获取数据
     *
     * @param level 缓存级别
	 * @param name 区域(库)
	 * @param key 键
     * @return
     * @since 2020-09-15
     */
    public static Object lpop(int level, String name, Object key) {
		if (name != null && key != null) {
			Cache cache = _GetCache(level, name, false);
			if (cache != null) {
				return cache.lpop(key);
			}
		}
		return null;
	}

    /**
     * 从列表的右边获取数据
     *
     * @param level 缓存级别
	 * @param name 区域(库)
	 * @param key 键
     * @return
     * @since 2020-09-15
     */
    public static Object rpop(int level, String name, Object key) {
		if (name != null && key != null) {
			Cache cache = _GetCache(level, name, false);
			if (cache != null) {
				return cache.rpop(key);
			}
		}
		return null;
	}

	/**
	 * 获取列表长度
	 *
	 * @param level 缓存级别
	 * @param name 区域(库)
	 * @param key 键
	 * @return
	 * @since 2020-10-12
	 */
	public static Object llen(byte level, String name, Object key) {
		if (name != null && key != null) {
			Cache cache = _GetCache(level, name, false);
			if (cache != null) {
				return cache.llen(key);
			}
		}
		return null;
	}

	/**
	 * 从列表中获取数据
	 *
	 * @param level 缓存级别
	 * @param name 区域(库)
	 * @param key 键
	 * @param count 个数
	 * @return
	 * @since 2020-10-10
	 */
	public static Object lrange(int level, String name, Object key, int count) {
		if (name != null && key != null) {
			Cache cache = _GetCache(level, name, false);
			if (cache != null) {
				return cache.lrange(key, count);
			}
		}
		return null;
	}

	/**
	 * 从列表中删除数据
	 *
	 * @param level 缓存级别
	 * @param name 区域(库)
	 * @param key 键
	 * @param count 个数
	 * @return
	 * @since 2020-10-12
	 */
	public static void lrem(byte level, String name, Object key, int count) {
		if (name != null && key != null) {
			Cache cache = _GetCache(level, name, false);
			if (cache != null) {
				cache.lrem(key, count);
			}
		}
	}

	/**
	 * 获取缓存中的数据
	 *
	 * @param level 缓存等级
	 * @param name 仓库
	 * @param key 键
	 * @param field 属性
	 * @return 值
	 * @since 2020-12-17
	 */
	public final static Object hget(int level, String name, Object key, String field) {
		if (name != null && key != null) {
			Cache cache = _GetCache(level, name, false);
			if (cache != null) {
				return cache.hget(key, field);
			}
		}
		return null;
	}

	/**
	 * 写入缓存
	 *
	 * @param level 缓存级别
	 * @param name 区域(库)
	 * @param key 键
	 * @param field 键
	 * @param value 属性
	 * @since 2020-12-17
	 */
	public final static void hput(int level, String name, Object key, String field, Object value) {
		if (name != null && key != null && value != null) {
			Cache cache = _GetCache(level, name, true);
			if (cache != null) {
				cache.hput(key, field, value);
			}
		}
	}

	/**
	 * 清除缓存中的某个数据
	 *
	 * @param level 缓存级别
	 * @param name 区域(库)
	 * @param key 键
	 * @param field 属性
	 * @since 2020-12-17
	 */
	public final static void hrem(int level, String name, Object key, String field) {
		if (name != null && key != null) {
			Cache cache = _GetCache(level, name, false);
			if (cache != null) {
				cache.hrem(key, field);
			}
		}
	}

	/**
	 * 校验缓存中的某个数据
	 *
	 * @param level 缓存级别
	 * @param name 区域(库)
	 * @param key 键
	 * @param field 属性
	 * @since 2022-03-28
	 */
	public final static Object hexists(int level, String name, Object key, String field) {
		if (name != null && key != null) {
			Cache cache = _GetCache(level, name, false);
			if (cache != null) {
				return cache.hexists(key, field);
			}
		}
		return null;
	}
}
