package com.jarveis.frame.cache.broadcast;

import com.jarveis.frame.cache.*;
import com.jarveis.frame.util.CharacterUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;
import redis.clients.util.Pool;
import redis.clients.util.SafeEncoder;

import java.net.URI;
import java.util.*;

/**
 * 缓存处理通道
 *
 * @author liuguojun
 * @since 2018-08-31
 */
public class RedisCacheChannel extends BinaryJedisPubSub implements
		CacheExpiredListener, CacheChannel {

	private static final Logger log = LoggerFactory.getLogger(RedisCacheChannel.class);

	private String name;
	private static String channel;
	private static RedisCacheChannel instance;
	private final Thread thread_subscribe;
	private static Pool<Jedis> pool;

	private void initPool(){
		Properties props = PuddleConfig.getRedisConfig();

		String mode = getProperty(props, "mode", "true");
		try {
			if ("sentinel".equals(mode)) {
				// 主备
				pool = getSentinelPool(props);
			} else {
				pool = getSinglePool(props);
			}
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
		}
	}

	/**
	 * 初始化缓存通道并连接
	 * 
	 * @param name
	 *            缓存实例名称
	 */
	private RedisCacheChannel(String name) throws CacheException {
		this.name = name;
		try {
			long ct = System.currentTimeMillis();
			CacheManager.initCacheProvider(this);

			channel = PuddleConfig.getRedisConfig()
					.getProperty("channel");

			thread_subscribe = new Thread(new Runnable() {
				public void run() {
					Jedis client = pool.getResource();
					try {
						client.subscribe(RedisCacheChannel.this,
								SafeEncoder.encode(channel));
					} finally {
						client.close();
					}
				}
			});

			thread_subscribe.start();

			log.info("Connected to channel:" + this.name + ", time "
					+ (System.currentTimeMillis() - ct) + " ms.");

		} catch (Exception e) {
			throw new CacheException(e);
		}
	}

	/**
	 * 单例方法
	 * 
	 * @return 返回 CacheChannel 单实例
	 */
	public final static RedisCacheChannel getInstance() {
		if (instance == null) {
			synchronized (RedisCacheChannel.class) {
				if (instance == null) {
					channel = PuddleConfig.getRedisConfig()
							.getProperty("redis.channel_name");
					instance = new RedisCacheChannel("default");
				}
			}
		}
		return instance;
	}

	@Override
	public void put(String region, Object key, Object value) {
		if (region != null && key != null) {
			if (value == null) {
				remove(region, key);
			} else {
				_sendRemoveCmd(region, key);// 清除原有的一级缓存的内容
				CacheManager.put(LEVEL_1, region, key, value);
				CacheManager.put(LEVEL_2, region, key, value);
			}
		}
		if (log.isDebugEnabled()) {
			log.debug("write data to cache region="+region+",key="+key+",value="+value);
		}
	}

	@Override
	public void put(String region, Object key, Object value, Integer expireInSec) {
		if (region != null && key != null) {
			if (value == null) {
				remove(region, key);
			} else {
				_sendRemoveCmd(region, key);// 清除原有的一级缓存的内容
				CacheManager.put(LEVEL_1, region, key, value, expireInSec);
				CacheManager.put(LEVEL_2, region, key, value, expireInSec);
			}
		}
		if (log.isDebugEnabled()) {
			log.debug("write data to cache region="+region+",key="+key+",value="+value);
		}
	}

	@Override
	public CacheObject get(String region, Object key) {
		CacheObject obj = new CacheObject();
		obj.setRegion(region);
		obj.setKey(key);
		if (region != null && key != null) {
			obj.setValue(CacheManager.get(LEVEL_1, region, key));
			if (obj.getValue() == null) {
				obj.setValue(CacheManager.get(LEVEL_2, region, key));
				if (obj.getValue() != null) {
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

	@Override
	public void remove(String region, Object key) {
		// 删除一级缓存
		CacheManager.remove(LEVEL_1, region, key);
		// 删除二级缓存
		CacheManager.remove(LEVEL_2, region, key);
		// 发送广播
		_sendRemoveCmd(region, key);
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

	@Override
	public void removeAll(String region, List keys) {
		CacheManager.removeAll(LEVEL_1, region, keys);
		CacheManager.removeAll(LEVEL_2, region, keys);
		_sendRemoveCmd(region, keys);
	}

	@Override
	public void clear(String region) {
		CacheManager.clear(LEVEL_1, region);
		CacheManager.clear(LEVEL_2, region);
		_sendClearCmd(region);
	}

	@Override
	public void close() {
		CacheManager.shutdown(LEVEL_1);
		if (isSubscribed()) {
			this.unsubscribe();
		}
		CacheManager.shutdown(LEVEL_2);
	}

	@Override
	public void lpush(String region, Object key, Object value) {
		if (region != null && key != null) {
			if (value == null) {
				remove(region, key);
			} else {
				//_sendRemoveCmd(region, key);// 清除原有的一级缓存的内容
				CacheManager.lpush(LEVEL_1, region, key, value);
				CacheManager.lpush(LEVEL_2, region, key, value);
			}
		}
		// log.info("write data to cache region="+region+",key="+key+",value="+value);
	}

	@Override
	public void rpush(String region, Object key, Object value) {
		if (region != null && key != null) {
			if (value == null) {
				remove(region, key);
			} else {
				//_sendRemoveCmd(region, key);// 清除原有的一级缓存的内容
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
			obj.setValue(CacheManager.hget(LEVEL_1, region, key, field));
			if (obj.getValue() == null) {
				obj.setValue(CacheManager.hget(LEVEL_2, region, key, field));
				if (obj.getValue() != null) {
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
				remove(region, key);
			} else {
				_sendRemoveCmd(region, getHKey(key, field));// 清除原有的一级缓存的内容
				CacheManager.hput(LEVEL_1, region, key, field, value);
				CacheManager.hput(LEVEL_2, region, key, field, value);
			}
		}
		if (log.isDebugEnabled()) {
			log.debug("write data to cache region="+region+",key="+key+",value="+value);
		}
	}

	@Override
	public void hrem(String region, Object key, String field) {
		// 删除一级缓存
		CacheManager.hrem(LEVEL_1, region, key, field);
		// 删除二级缓存
		CacheManager.hrem(LEVEL_2, region, key, field);
		// 发送广播
		_sendRemoveCmd(region, getHKey(key, field));
	}

	private String getHKey(Object key, String field){
		return key.toString() + "$" + field;
	}

	@Override
	public void notifyElementExpired(String region, Object key) {
		if (log.isDebugEnabled()) {
			log.debug("Cache data expired, region=" + region + ",key=" + key);
		}
		// 删除二级缓存
		if (key instanceof List) {
			CacheManager.removeAll(LEVEL_2, region, (List) key);
		} else {
			CacheManager.remove(LEVEL_2, region, key);
		}
		// 发送广播
		_sendRemoveCmd(region, key);
	}

	/**
	 * 发送清除缓存的广播命令
	 * 
	 * @param region 区域(库)
	 * @param key 键
	 */
	private void _sendRemoveCmd(String region, Object key) {
		// 发送广播
		Command cmd = new Command(Command.OPT_DELETE_KEY, region, key);
		Jedis client = pool.getResource();
		try {
			client.publish(SafeEncoder.encode(channel), cmd.toBuffers());
		} catch (Exception e) {
			log.error("Unable to delete cache,region=" + region + ",key="
					+ key, e);
		} finally {
			client.close();
		}
	}

	/**
	 * 发送清除缓存的广播命令
	 * 
	 * @param region 区域(库)
	 */
	private void _sendClearCmd(String region) {
		// 发送广播
		Command cmd = new Command(Command.OPT_CLEAR_KEY, region, "");
		Jedis client = pool.getResource();
		try {
			client.publish(SafeEncoder.encode(channel), cmd.toBuffers());
		} catch (Exception e) {
			log.error("Unable to clear cache,region=" + region, e);
		} finally {
			client.close();
		}
	}

	/**
	 * 事件消息（有订阅消息时触发）
	 *
	 * @param channel 订阅消息的通道
	 * @param message 消息
	 */
	public void onMessage(byte[] channel, byte[] message) {
		// 无效消息
		if (message != null && message.length <= 0) {
			log.warn("Message is empty.");
			return;
		}

		try {
			Command cmd = Command.parse(message);

			if (cmd == null || cmd.isLocalCommand())
				return;

			switch (cmd.getOperator()) {
			case Command.OPT_DELETE_KEY:
				onDeleteCacheKey(cmd.getRegion(), cmd.getKey());
				break;
			case Command.OPT_CLEAR_KEY:
				onClearCacheKey(cmd.getRegion());
				break;
			default:
				log.warn("Unknown message type = " + cmd.getOperator());
			}
		} catch (Exception e) {
			log.error("Unable to handle received msg", e);
		}
	}

	/**
	 * 删除一级缓存的键对应内容
	 * 
	 * @param region 区域(库)
	 * @param key 键
	 */
	protected void onDeleteCacheKey(String region, Object key) {
		if (key instanceof List) {
			CacheManager.removeAll(LEVEL_1, region, (List) key);
		} else {
			CacheManager.remove(LEVEL_1, region, key);
		}
		
		if (log.isDebugEnabled()) {
			log.debug("Received cache evict message, region=" + region + ",key=" + key);
		}
	}

	/**
	 * 清除一级缓存的键对应内容
	 * 
	 * @param region 区域(库)
	 */
	protected void onClearCacheKey(String region) {
		CacheManager.clear(LEVEL_1, region);
		if (log.isDebugEnabled()) {
			log.debug("Received cache clear message, region=" + region);
		}
	}

	/**
	 * 获取配置
	 *
	 * @param props 配置信息
	 * @param key 配置键
	 * @param defaultValue 默认值
	 * @return 配置键对应的值
	 */
	private static String getProperty(Properties props, String key, String defaultValue) {
		return props.getProperty(key, defaultValue).trim();
	}

	/**
	 * 获取配置
	 *
	 * @param props 配置信息
	 * @param key 配置键
	 * @param defaultValue 默认值
	 * @return 配置键对应的值
	 */
	private static int getProperty(Properties props, String key, int defaultValue) {
		try {
			return Integer.parseInt(props.getProperty(key, String.valueOf(defaultValue)).trim());
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * 获取配置
	 *
	 * @param props 配置信息
	 * @param key 配置键
	 * @param defaultValue 默认值
	 * @return 配置键对应的值
	 */
	private static boolean getProperty(Properties props, String key, boolean defaultValue) {
		return "true".equalsIgnoreCase(props.getProperty(key, String.valueOf(defaultValue)).trim());
	}

	/**
	 * 获取连接池配置
	 *
	 * @param prop 配置信息
	 * @return redis连接池的配置对象
	 */
	private JedisPoolConfig getPoolConfig(Properties prop) {
		int maxTotal = NumberUtils.toInt(prop.getProperty("maxTotal", "100"));
		int maxIdle = NumberUtils.toInt(prop.getProperty("maxIdle", "20"));
		int maxWait = NumberUtils.toInt(prop.getProperty("maxWait", "80"));
		boolean testOnBorrow = Boolean.valueOf(prop.getProperty("testOnBorrow"));
		boolean testOnReturn = Boolean.valueOf(prop.getProperty("testOnReturn"));

		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(maxTotal);
		config.setMaxIdle(maxIdle);
		config.setMaxWaitMillis(maxWait);
		config.setTestOnBorrow(testOnBorrow);
		config.setTestOnReturn(testOnReturn);

		return config;
	}

	/**
	 * 解析单机redis配置, 并创建连接池
	 *
	 * @see redis.clients.jedis.JedisPool
	 * @see redis.clients.jedis.Jedis
	 * @param prop 配置信息
	 * @throws Exception
	 * @return　JedisPool redis连接池对象
	 */
	private JedisPool getSinglePool(Properties prop) throws Exception {
		// 连接池配置对象
		JedisPoolConfig poolConfig = getPoolConfig(prop);

		int timeout = getProperty(prop, "timeout", 20000);
		String uri = prop.getProperty("uri");
		// 创建集群节点
		String[] uriArr = StringUtils.split(uri, CharacterUtil.SEPARATOR);

		JedisPool jedisPool = new JedisPool(poolConfig, new URI(uriArr[0]), timeout);

		return jedisPool;
	}

	/**
	 * 解析主备配置, 并创建连接池
	 *
	 * @see redis.clients.jedis.JedisSentinelPool
	 * @see redis.clients.jedis.Jedis
	 * @param prop　配置信息
	 * @return
	 * @throws Exception
	 */
	private JedisSentinelPool getSentinelPool(Properties prop)
			throws Exception {
		// 连接池配置对象
		JedisPoolConfig poolConfig = getPoolConfig(prop);

		String uri = prop.getProperty("uri");
		int timeout = NumberUtils.toInt(prop.getProperty("timeout", "2000"));
		// 创建集群节点
		String[] uriArr = StringUtils.split(uri, CharacterUtil.SEPARATOR);

		Set<String> hosts = new HashSet<String>();
		for (int i = 0; i < uriArr.length; i++) {
			if (StringUtils.isEmpty(uriArr[i])) {
				continue;
			}
			URI u = new URI(uriArr[i]);
			hosts.add(u.getHost() + ":" + u.getPort());
		}
		JedisSentinelPool pool = new JedisSentinelPool(UUID.randomUUID()
				.toString(), hosts, poolConfig, timeout);

		return pool;
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
