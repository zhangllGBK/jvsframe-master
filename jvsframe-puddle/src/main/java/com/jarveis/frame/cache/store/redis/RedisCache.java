package com.jarveis.frame.cache.store.redis;

import com.jarveis.frame.cache.Cache;
import com.jarveis.frame.cache.CacheException;
import com.jarveis.frame.cache.serializer.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * redis缓存
 *
 * @author liuguojun
 * @since 2018-08-31
 */
public class RedisCache implements Cache {

	private static final Logger log = LoggerFactory.getLogger(RedisCache.class);

	protected String region;

	public RedisCache(String region) {
		this.region = region;
	}

	private byte[] getKeys() {
		return (region + ":*").getBytes();
	}

	private byte[] getKey(Object key) {
		return (region + ":" + key.toString()).getBytes();
	}

	public void put(Object key, Object value) {
		if (key == null) {
			return;
		}
		if (value == null) {
			remove(key);
		} else {
			RedisClient client = RedisCacheProvider.getResource();
			try {
				client.getCommands().set(getKey(key), SerializationUtils.serialize(value));
			} catch (Exception e) {
				throw new CacheException(e);
			} finally {
				RedisCacheProvider.returnResource(client);
			}
		}
	}

	public void put(Object key, Object value, Integer expireInSec) {
		if (key == null) {
			return;
		}
		if (value == null) {
			remove(key);
		} else {
			RedisClient client = RedisCacheProvider.getResource();
			try {
				client.getCommands().setex(getKey(key), expireInSec,
						SerializationUtils.serialize(value));
			} catch (Exception e) {
				throw new CacheException(e);
			} finally {
				RedisCacheProvider.returnResource(client);
			}
		}
	}

	public Object get(Object key) {
		if (null == key) {
			return null;
		}
		Object obj = null;
		RedisClient client = RedisCacheProvider.getResource();
		try {
			byte[] b = client.getCommands().get(getKey(key));
			if (b != null) {
				obj = SerializationUtils.deserialize(b);
			}
		} catch (Exception e) {
			log.error("Error occured when get data from redis2 cache", e);
			if (e instanceof IOException || e instanceof NullPointerException) {
				remove(key);
			}
		} finally {
			RedisCacheProvider.returnResource(client);
		}
		return obj;
	}

	public void remove(Object key) {
		if (key == null) {
			return;
		}
		RedisClient client = RedisCacheProvider.getResource();
		try {
			client.getCommands().del(getKey(key));
		} catch (Exception e) {
			throw new CacheException(e);
		} finally {
			RedisCacheProvider.returnResource(client);
		}
	}

	@Override
	public Object exists(Object key) {
		if (null == key) {
			return null;
		}
		Object obj = null;
		RedisClient client = RedisCacheProvider.getResource();
		try {
			Boolean b = client.getCommands().exists(getKey(key));
			if (b != null) {
				obj = b;
			} else {
				obj = Boolean.FALSE;
			}
		} catch (Exception e) {
			log.error("Error occured when get data from redis2 cache", e);
		} finally {
			RedisCacheProvider.returnResource(client);
		}
		return obj;
	}

	public void removeAll(List keys) {
		if (keys == null || keys.size() == 0)
			return;

		RedisClient client = RedisCacheProvider.getResource();
		try {
			int size = keys.size();
			for (int i = 0; i < size; i++) {
				client.getCommands().del(getKey(keys.get(i)));
			}

		} catch (Exception e) {
			throw new CacheException(e);
		} finally {
			RedisCacheProvider.returnResource(client);
		}
	}

	public void clear() {
		RedisClient client = RedisCacheProvider.getResource();
		try {
			client.getCommands().del(getKeys());
		} catch (Exception e) {
			throw new CacheException(e);
		} finally {
			RedisCacheProvider.returnResource(client);
		}
	}

	public void destroy() {
		this.clear();
	}

	@Override
	public void lpush(Object key, Object value) {
		if (key == null) {
			return;
		}
		RedisClient client = RedisCacheProvider.getResource();
		try {
			client.getCommands().lpush(getKey(key), SerializationUtils.serialize(value));
		} catch (Exception e) {
			throw new CacheException(e);
		} finally {
			RedisCacheProvider.returnResource(client);
		}
	}

	@Override
	public void rpush(Object key, Object value) {
		if (key == null) {
			return;
		}
		RedisClient client = RedisCacheProvider.getResource();
		try {
			client.getCommands().rpush(getKey(key), SerializationUtils.serialize(value));
		} catch (Exception e) {
			throw new CacheException(e);
		} finally {
			RedisCacheProvider.returnResource(client);
		}
	}

	@Override
	public Object lpop(Object key) {
		if (null == key) {
			return null;
		}
		Object obj = null;
		RedisClient client = RedisCacheProvider.getResource();
		try {
			byte[] b = client.getCommands().lpop(getKey(key));
			if (b != null) {
				obj = SerializationUtils.deserialize(b);
			}
		} catch (Exception e) {
			log.error("Error occured when get data from redis cache", e);
		} finally {
			RedisCacheProvider.returnResource(client);
		}
		return obj;
	}

	@Override
	public Object rpop(Object key) {
		if (null == key) {
			return null;
		}
		Object obj = null;
		RedisClient client = RedisCacheProvider.getResource();
		try {
			byte[] b = client.getCommands().rpop(getKey(key));
			if (b != null) {
				obj = SerializationUtils.deserialize(b);
			}
		} catch (Exception e) {
			log.error("Error occured when get data from redis cache", e);
		} finally {
			RedisCacheProvider.returnResource(client);
		}
		return obj;
	}

	@Override
	public Object llen(Object key) {
		if (null == key) {
			return null;
		}
		Object obj = null;
		RedisClient client = RedisCacheProvider.getResource();
		try {
			long llen = client.getCommands().llen(getKey(key));
			obj = Long.valueOf(llen);
		} catch (Exception e) {
			log.error("Error occured when get data from redis cache", e);
		} finally {
			RedisCacheProvider.returnResource(client);
		}
		return obj;
	}

	@Override
	public Object lrange(Object key, int count) {
		if (null == key) {
			return null;
		}
		List reList = new ArrayList();
		RedisClient client = RedisCacheProvider.getResource();
		try {
			List<byte[]> list = client.getCommands().lrange(getKey(key), 0, count-1);
			for (byte[] b: list) {
				reList.add(SerializationUtils.deserialize(b));
			}
		} catch (Exception e) {
			log.error("Error occured when get data from redis cache", e);
		} finally {
			RedisCacheProvider.returnResource(client);
		}
		return reList;
	}

	@Override
	public void lrem(Object key, int count) {
		if (null != key) {
			RedisClient client = RedisCacheProvider.getResource();
			try {
				client.getCommands().ltrim(getKey(key), count, -1);
			} catch (Exception e) {
				throw new CacheException(e);
			} finally {
				RedisCacheProvider.returnResource(client);
			}
		}
	}

	@Override
	public Object hget(Object key, String field) {
		if (null == key) {
			return null;
		}
		Object obj = null;
		RedisClient client = RedisCacheProvider.getResource();
		try {
			byte[] b = client.getCommands().hget(getKey(key), SerializationUtils.serialize(field));
			if (b != null) {
				obj = SerializationUtils.deserialize(b);
			}
		} catch (Exception e) {
			log.error("Error occured when get data from redis2 cache", e);
			if (e instanceof IOException || e instanceof NullPointerException) {
				hrem(key, field);
			}
		} finally {
			RedisCacheProvider.returnResource(client);
		}
		return obj;
	}

	@Override
	public void hput(Object key, String field, Object value) {
		if (key == null) {
			return;
		}
		if (value == null) {
			hrem(key, field);
		} else {
			RedisClient client = RedisCacheProvider.getResource();
			try {
				client.getCommands().hset(getKey(key), SerializationUtils.serialize(field), SerializationUtils.serialize(value));
			} catch (Exception e) {
				throw new CacheException(e);
			} finally {
				RedisCacheProvider.returnResource(client);
			}
		}
	}

	@Override
	public void hrem(Object key, String field) {
		if (key == null) {
			return;
		}
		RedisClient client = RedisCacheProvider.getResource();
		try {
			client.getCommands().hdel(getKey(key), SerializationUtils.serialize(field));
		} catch (Exception e) {
			throw new CacheException(e);
		} finally {
			RedisCacheProvider.returnResource(client);
		}
	}

	@Override
	public Object hexists(Object key, String field) {
		if (null == key) {
			return null;
		}
		Object obj = null;
		RedisClient client = RedisCacheProvider.getResource();
		try {
			Boolean b = client.getCommands().hexists(getKey(key), SerializationUtils.serialize(field));
			if (b != null) {
				obj = b;
			} else {
				obj = Boolean.FALSE;
			}
		} catch (Exception e) {
			log.error("Error occured when get data from redis2 cache", e);
		} finally {
			RedisCacheProvider.returnResource(client);
		}
		return obj;
	}

}
