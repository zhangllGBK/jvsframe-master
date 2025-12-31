package com.jarveis.frame.cache.store.redis;

import com.jarveis.frame.cache.Cache;
import com.jarveis.frame.cache.CacheExpiredListener;
import com.jarveis.frame.cache.CacheProvider;
import com.jarveis.frame.cache.PuddleConfig;
import com.jarveis.frame.util.CharacterUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;
import redis.clients.util.Pool;

import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * redis缓存的供应商
 *
 * @author liuguojun
 * @since 2018-08-31
 */
public class RedisCacheProvider implements CacheProvider {

    private static final Logger log = LoggerFactory.getLogger(RedisCacheProvider.class);

    protected ConcurrentHashMap<String, RedisCache> caches = new ConcurrentHashMap<>();
    private static Object pool;

    /**
     * 获取redis客户端连接
     *
     * @return redis客户端连接
     */
    public static RedisClient getResource() {
        if (pool instanceof Pool) {
            Object conn = ((Pool) pool).getResource();
            return new RedisClient(conn);
        } else {
            return new RedisClient(pool);
        }
    }

    /**
     * 释放redis客户端连接
     *
     * @param client 客户端连接
     */
    public static void returnResource(RedisClient client) {
        if (pool instanceof Pool) {
            // 连接池需要释放数据源
            if (null == client) {
                return;
            }
            try {
                client.getCloseable().close();
            } catch (IOException ex) {
                log.error(ex.getMessage(), ex);
            }
        }
    }

    public String name() {
        return "redis";
    }

    public Cache buildCache(String regionName, boolean autoCreate, CacheExpiredListener listener) {
        if (caches.get(regionName) == null) {
            caches.put(regionName, new RedisCache(regionName));
        }
        return caches.get(regionName);
    }

    public void start() {
        Properties props = PuddleConfig.getRedisConfig();

        String mode = getProperty(props, "mode", "single");
        try {
            if ("shared".equals(mode)) {
                // 分区
                pool = getShardedPool(props);
            } else if ("cluster".equals(mode)) {
                // 集群
                pool = getClusterPool(props);
            } else if ("sentinel".equals(mode)) {
                // 主备
                pool = getSentinelPool(props);
            } else {
                // 单机
                pool = getSinglePool(props);
            }
            if (pool == null) {
                throw new Exception("redis pool initialization failed!");
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    public void stop() {
        //pool.destroy();
        caches.clear();
    }

    private String getProperty(Properties props, String key, String defaultValue) {
        return props.getProperty(key, defaultValue).trim();
    }

    private int getProperty(Properties props, String key, int defaultValue) {
        try {
            return Integer.parseInt(props.getProperty(key, String.valueOf(defaultValue)).trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private boolean getProperty(Properties props, String key, boolean defaultValue) {
        return "true".equalsIgnoreCase(props.getProperty(key, String.valueOf(defaultValue)).trim());
    }

    /**
     * 获取连接池配置
     *
     * @param prop 配置信息
     * @return
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
     * @param prop 配置信息
     * @throws Exception
     * @return　JedisPool
     * @see redis.clients.jedis.JedisPool
     * @see redis.clients.jedis.Jedis
     */
    private JedisPool getSinglePool(Properties prop) throws Exception {
        JedisPool jedisPool = null;
        // 连接池配置对象
        JedisPoolConfig poolConfig = getPoolConfig(prop);

        int timeout = getProperty(prop, "timeout", 20000);
        String uriStr = prop.getProperty("uri");
        // 创建集群节点
        String[] uriArr = StringUtils.split(uriStr, CharacterUtil.SEPARATOR);
        if (uriArr.length > 0) {
            URI uri = new URI(uriArr[0]);
            if (StringUtils.isNotEmpty(uri.getUserInfo())) {
                jedisPool = new JedisPool(poolConfig, uri.getHost(), uri.getPort(), timeout, uri.getUserInfo());
            } else {
                jedisPool = new JedisPool(poolConfig, uri.getHost(), uri.getPort(), timeout);
            }
        } else {
            throw new Exception("redis uri not found!");
        }
        return jedisPool;
    }

    /**
     * 解析分区配置, 并创建连接池
     *
     * @param prop 配置信息
     * @return
     * @throws Exception
     * @see redis.clients.jedis.ShardedJedisPool
     * @see redis.clients.jedis.ShardedJedis
     */
    private ShardedJedisPool getShardedPool(Properties prop) throws Exception {
        // 连接池配置对象
        JedisPoolConfig poolConfig = getPoolConfig(prop);

        String uri = prop.getProperty("uri");
        int timeout = getProperty(prop, "timeout", 20000);
        // 创建集群节点
        String[] uriArr = StringUtils.split(uri, CharacterUtil.SEPARATOR);
        List<JedisShardInfo> shareInfos = new ArrayList<JedisShardInfo>();
        for (int i = 0; i < uriArr.length; i++) {
            if (StringUtils.isEmpty(uriArr[i])) {
                continue;
            }
            JedisShardInfo jedisShardInfo = new JedisShardInfo(new URI(uriArr[i]));
            jedisShardInfo.setSoTimeout(timeout);
            shareInfos.add(jedisShardInfo);
        }

        // 创建集群连接池
        ShardedJedisPool shardedJedisPool = new ShardedJedisPool(poolConfig, shareInfos);

        return shardedJedisPool;
    }

    /**
     * 解析集群配置, 并创建连接池
     *
     * @param prop 配置信息
     * @return
     * @throws Exception
     * @see redis.clients.jedis.JedisCluster
     */
    private JedisCluster getClusterPool(Properties prop) throws Exception {
        // 连接池配置对象
        JedisPoolConfig poolConfig = getPoolConfig(prop);

        String uri = prop.getProperty("uri");
        String password = null;
        int timeout = NumberUtils.toInt(prop.getProperty("timeout", "2000"));
        // 创建集群节点
        String[] uriArr = StringUtils.split(uri, CharacterUtil.SEPARATOR);

        Set<HostAndPort> hosts = new HashSet<HostAndPort>();
        for (int i = 0; i < uriArr.length; i++) {
            if (StringUtils.isEmpty(uriArr[i])) {
                continue;
            }
            URI u = new URI(uriArr[i]);
            if (password == null) {
                password = u.getUserInfo();
            }
            hosts.add(new HostAndPort(u.getHost(), u.getPort()));
        }

        JedisCluster cluster = null;
        if (password == null) {
            cluster = new JedisCluster(hosts, timeout, poolConfig);
        } else {
            cluster = new JedisCluster(hosts, timeout, timeout, 5, password, poolConfig);
        }

        return cluster;
    }

    /**
     * 解析主备配置, 并创建连接池
     *
     * @param prop 配置信息
     * @return
     * @throws Exception
     * @see redis.clients.jedis.JedisSentinelPool
     * @see redis.clients.jedis.Jedis
     */
    private JedisSentinelPool getSentinelPool(Properties prop) throws Exception {
        // 连接池配置对象
        JedisPoolConfig poolConfig = getPoolConfig(prop);

        String uri = prop.getProperty("uri");
        String password = null;
        int timeout = NumberUtils.toInt(prop.getProperty("timeout", "2000"));
        // 创建集群节点
        String[] uriArr = StringUtils.split(uri, CharacterUtil.SEPARATOR);

        Set<String> hosts = new HashSet<String>();
        for (int i = 0; i < uriArr.length; i++) {
            if (StringUtils.isEmpty(uriArr[i])) {
                continue;
            }
            URI u = new URI(uriArr[i]);
            if (password == null) {
                password = u.getUserInfo();
            }
            hosts.add(u.getHost() + ":" + u.getPort());
        }
        JedisSentinelPool pool = null;
        if (password == null) {
            pool = new JedisSentinelPool("mymaster", hosts, poolConfig, timeout);
        } else {
            pool = new JedisSentinelPool("mymaster", hosts, poolConfig, timeout, password);
        }
        return pool;
    }

}
