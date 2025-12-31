package com.jarveis.frame.cache.broadcast;

import com.jarveis.frame.cache.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 缓存处理通道
 *
 * @author liuguojun
 * @since  2018-08-31
 */
public class NoneCacheChannel implements CacheExpiredListener, CacheChannel {

	private static final Logger log = LoggerFactory.getLogger(NoneCacheChannel.class);

	private String name;
	private static NoneCacheChannel instance;

	/**
	 * 初始化缓存通道并连接
	 * 
	 * @param name 缓存实例名称
	 */
	private NoneCacheChannel(String name) {
		this.name = name;
		try {
			long ct = System.currentTimeMillis();
			CacheManager.initCacheProvider(this);

			log.info("Connected to channel:" + this.name + ", time " + (System.currentTimeMillis() - ct) + " ms.");
		} catch (Exception e) {
			throw new CacheException(e);
		}
	}

	/**
	 * 单例方法
	 * 
	 * @return 返回 CacheChannel 单实例
	 */
	public final static NoneCacheChannel getInstance() {
		if (instance == null) {
			synchronized (NoneCacheChannel.class) {
				if (instance == null) {
					instance = new NoneCacheChannel("default");
				}
			}
		}
		return instance;
	}

	public void put(String region, Object key, Object value) {
		if (region != null && key != null) {
			if (value == null) {
				remove(region, key);
			} else {
				// 设置缓存到L1
				CacheManager.put(LEVEL_1, region, key, value);
				// 设置缓存到L2
				CacheManager.put(LEVEL_2, region, key, value);
			}
		}
	}

	public void put(String region, Object key, Object value, Integer expireInSec) {
		if (region != null && key != null) {
			if (value == null) {
				remove(region, key);
			} else {
				// 设置缓存到L1
				CacheManager.put(LEVEL_1, region, key, value, expireInSec);
				// 设置缓存到L2
				CacheManager.put(LEVEL_2, region, key, value, expireInSec);
			}
		}
	}

	public CacheObject get(String region, Object key) {
		CacheObject obj = new CacheObject();
		obj.setRegion(region);
		obj.setKey(key);
		if (region != null && key != null) {
			// 从L1中取数据
			obj.setValue(CacheManager.get(LEVEL_1, region, key));
			if (obj.getValue() == null) {
				// 如果L1获取数据为null,则从L2获取数据
				obj.setValue(CacheManager.get(LEVEL_2, region, key));
				if (obj.getValue() != null) {
					// 如果L2数据不为null,则向L1中添加数据
					obj.setLevel(LEVEL_2);
					CacheManager.put(LEVEL_1, region, key, obj.getValue());
				}
			} else {
				obj.setLevel(LEVEL_1);
			}
		}
		if (log.isDebugEnabled()) {
			log.debug(obj.toString());
		}
		return obj;
	}

	public void remove(String region, Object key) {
		// 删除L1缓存
		CacheManager.remove(LEVEL_1, region, key);
		// 删除L2缓存
		CacheManager.remove(LEVEL_2, region, key);
	}

	@Override
	public CacheObject exists(String region, Object key) {
		CacheObject obj = new CacheObject();
		obj.setRegion(region);
		obj.setKey(key);
		if (region != null && key != null) {
			obj.setValue(CacheManager.exists(LEVEL_1, region, key));
			if (obj.getValue() == null) {
				obj.setValue(CacheManager.exists(LEVEL_2, region, key));
				if (obj.getValue() != null) {
					obj.setLevel(LEVEL_2);
				}
			} else {
				obj.setLevel(LEVEL_1);
			}
		}
		if (log.isDebugEnabled()) {
			log.debug(obj.toString());
		}
		return obj;
	}

	public void removeAll(String region, List keys) {
		// 删除L1缓存
		CacheManager.removeAll(LEVEL_1, region, keys);
		// 删除L2缓存
		CacheManager.removeAll(LEVEL_2, region, keys);
	}

	public void clear(String region) {
		// 清空L1缓存
		CacheManager.clear(LEVEL_1, region);
		// 清空L2缓存
		CacheManager.clear(LEVEL_2, region);
	}

	public void close() {
		// 关闭L1缓存
		CacheManager.shutdown(LEVEL_1);
		// 关闭L2缓存
		CacheManager.shutdown(LEVEL_2);
	}

	@Override
	public void lpush(String region, Object key, Object value) {
		if (region != null && key != null) {
			if (value == null) {
				remove(region, key);
			} else {
				CacheManager.lpush(LEVEL_1, region, key, value);
				CacheManager.lpush(LEVEL_2, region, key, value);
			}
		}
	}

	@Override
	public void rpush(String region, Object key, Object value) {
		if (region != null && key != null) {
			if (value == null) {
				remove(region, key);
			} else {
				CacheManager.rpush(LEVEL_1, region, key, value);
				CacheManager.rpush(LEVEL_2, region, key, value);
			}
		}
	}

	@Override
	public CacheObject lpop(String region, Object key) {
		CacheObject obj = new CacheObject();
		obj.setRegion(region);
		obj.setKey(key);
		if (region != null && key != null) {
			obj.setValue(CacheManager.lpop(LEVEL_1, region, key));
			if (obj.getValue() == null) {
				obj.setValue(CacheManager.lpop(LEVEL_2, region, key));
				if (obj.getValue() != null) {
					obj.setLevel(LEVEL_2);
				}
			} else {
				obj.setLevel(LEVEL_1);
			}
		}
		if (log.isDebugEnabled()) {
			log.debug(obj.toString());
		}
		return obj;
	}

	@Override
	public CacheObject rpop(String region, Object key) {
		CacheObject obj = new CacheObject();
		obj.setRegion(region);
		obj.setKey(key);
		if (region != null && key != null) {
			obj.setValue(CacheManager.rpop(LEVEL_1, region, key));
			if (obj.getValue() == null) {
				obj.setValue(CacheManager.rpop(LEVEL_2, region, key));
				if (obj.getValue() != null) {
					obj.setLevel(LEVEL_2);
				}
			} else {
				obj.setLevel(LEVEL_1);
			}
		}
		if (log.isDebugEnabled()) {
			log.debug(obj.toString());
		}
		return obj;
	}

	@Override
	public CacheObject llen(String region, Object key) {
		CacheObject obj = new CacheObject();
		obj.setRegion(region);
		obj.setKey(key);
		if (region != null && key != null) {
			obj.setValue(CacheManager.llen(LEVEL_1, region, key));
			if (obj.getValue() == null) {
				obj.setValue(CacheManager.llen(LEVEL_2, region, key));
				if (obj.getValue() != null) {
					obj.setLevel(LEVEL_2);
				}
			} else {
				obj.setLevel(LEVEL_1);
			}
		}
		if (log.isDebugEnabled()) {
			log.debug(obj.toString());
		}
		return obj;
	}

	@Override
	public CacheObject lrange(String region, Object key, int count) {
		CacheObject obj = new CacheObject();
		obj.setRegion(region);
		obj.setKey(key);
		if (region != null && key != null) {
			obj.setValue(CacheManager.lrange(LEVEL_1, region, key, count));
			if (obj.getValue() == null) {
				obj.setValue(CacheManager.lrange(LEVEL_2, region, key, count));
				if (obj.getValue() != null) {
					obj.setLevel(LEVEL_2);
				}
			} else {
				obj.setLevel(LEVEL_1);
			}
		}
		if (log.isDebugEnabled()) {
			log.debug(obj.toString());
		}
		return obj;
	}

	@Override
	public void lrem(String region, Object key, int count) {
		if (region != null && key != null) {
			CacheManager.lrem(LEVEL_1, region, key, count);
			CacheManager.lrem(LEVEL_2, region, key, count);
		}
	}

	@Override
	public CacheObject hget(String region, Object key, String field) {
		CacheObject obj = new CacheObject();
		obj.setRegion(region);
		obj.setKey(key);
		obj.setField(field);
		if (region != null && key != null) {
			// 从L1中取数据
			obj.setValue(CacheManager.hget(LEVEL_1, region, key, field));
			if (obj.getValue() == null) {
				// 如果L1获取数据为null,则从L2获取数据
				obj.setValue(CacheManager.hget(LEVEL_2, region, key, field));
				if (obj.getValue() != null) {
					// 如果L2数据不为null,则向L1中添加数据
					obj.setLevel(LEVEL_2);
					CacheManager.hput(LEVEL_1, region, key, field, obj.getValue());
				}
			} else {
				obj.setLevel(LEVEL_1);
			}
		}
		if (log.isDebugEnabled()) {
			log.debug(obj.toString());
		}
		return obj;
	}

	@Override
	public void hput(String region, Object key, String field, Object value) {
		if (region != null && key != null) {
			if (value == null) {
				hrem(region, key, field);
			} else {
				// 设置缓存到L1
				CacheManager.hput(LEVEL_1, region, key, field, value);
				// 设置缓存到L2
				CacheManager.hput(LEVEL_2, region, key, field, value);
			}
		}
	}

	@Override
	public void hrem(String region, Object key, String field) {
		// 删除L1缓存
		CacheManager.hrem(LEVEL_1, region, key, field);
		// 删除L2缓存
		CacheManager.hrem(LEVEL_2, region, key, field);
	}

	public void notifyElementExpired(String region, Object key) {
	}

	@Override
	public CacheObject hexists(String region, Object key, String field) {
		CacheObject obj = new CacheObject();
		obj.setRegion(region);
		obj.setKey(key);
		if (region != null && key != null) {
			obj.setValue(CacheManager.hexists(LEVEL_1, region, key, field));
			if (obj.getValue() == null) {
				obj.setValue(CacheManager.hexists(LEVEL_2, region, key, field));
				if (obj.getValue() != null) {
					obj.setLevel(LEVEL_2);
				}
			} else {
				obj.setLevel(LEVEL_1);
			}
		}
		if (log.isDebugEnabled()) {
			log.debug(obj.toString());
		}
		return obj;
	}
}
