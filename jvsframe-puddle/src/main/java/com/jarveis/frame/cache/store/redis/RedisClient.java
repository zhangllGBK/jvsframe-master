package com.jarveis.frame.cache.store.redis;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;
import redis.clients.jedis.params.geo.GeoRadiusParam;
import redis.clients.jedis.params.sortedset.ZAddParams;
import redis.clients.jedis.params.sortedset.ZIncrByParams;

import java.io.Closeable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * RedisClient支持单机模式、主备、集群模式
 *
 * @author liuguojun
 * @since 2019-12-27
 */
public class RedisClient {

    private static final Logger log = LoggerFactory.getLogger(RedisClient.class);

    private BinaryJedisCommands commands;
    private Closeable closeable;

    public RedisClient(Object obj) {
        if (obj instanceof BinaryJedisCommands) {
            commands = (BinaryJedisCommands) obj;
        } else if (obj instanceof JedisCluster) {
            commands = buildCommands((JedisCluster)obj);
        } else {
            log.error("obj not BinaryJedisCommands and JedisCluster instance.", new Exception());
        }

        if (obj instanceof Closeable) {
            closeable = (Closeable) obj;
        } else {
            log.error("obj not Closeable instance.", new Exception());
        }
    }

    public BinaryJedisCommands getCommands() {
        return commands;
    }

    public Closeable getCloseable() {
        return closeable;
    }

    private BinaryJedisCommands buildCommands(JedisCluster obj) {
        return new BinaryJedisCommands() {

            @Override
            public String set(byte[] key, byte[] value) {
                return obj.set(key, value);
            }

            @Deprecated
            @Override
            public String set(byte[] key, byte[] value, byte[] nxxx) {
                return StringUtils.EMPTY;
            }

            @Override
            public String set(byte[] key, byte[] value, byte[] nxxx, byte[] expx, long time) {
                return obj.set(key, value, nxxx, expx, time);
            }

            @Override
            public byte[] get(byte[] key) {
                return obj.get(key);
            }

            @Override
            public Boolean exists(byte[] key) {
                return obj.exists(key);
            }

            @Override
            public Long persist(byte[] key) {
                return obj.persist(key);
            }

            @Override
            public String type(byte[] key) {
                return obj.type(key);
            }

            @Override
            public Long expire(byte[] key, int seconds) {
                return obj.expire(key, seconds);
            }

            @Override
            public Long pexpire(String key, long milliseconds) {
                return obj.pexpire(key, milliseconds);
            }

            @Override
            public Long pexpire(byte[] key, long milliseconds) {
                return obj.pexpire(key, milliseconds);
            }

            @Override
            public Long expireAt(byte[] key, long unixTime) {
                return obj.expireAt(key, unixTime);
            }

            @Override
            public Long pexpireAt(byte[] key, long millisecondsTimestamp) {
                return obj.pexpireAt(key, millisecondsTimestamp);
            }

            @Override
            public Long ttl(byte[] key) {
                return obj.ttl(key);
            }

            @Override
            public Boolean setbit(byte[] key, long offset, boolean value) {
                return obj.setbit(key, offset, value);
            }

            @Override
            public Boolean setbit(byte[] key, long offset, byte[] value) {
                return obj.setbit(key, offset, value);
            }

            @Override
            public Boolean getbit(byte[] key, long offset) {
                return obj.getbit(key, offset);
            }

            @Override
            public Long setrange(byte[] key, long offset, byte[] value) {
                return obj.setrange(key, offset, value);
            }

            @Override
            public byte[] getrange(byte[] key, long startOffset, long endOffset) {
                return obj.getrange(key, startOffset, endOffset);
            }

            @Override
            public byte[] getSet(byte[] key, byte[] value) {
                return obj.getSet(key, value);
            }

            @Override
            public Long setnx(byte[] key, byte[] value) {
                return obj.setnx(key, value);
            }

            @Override
            public String setex(byte[] key, int seconds, byte[] value) {
                return obj.setex(key, seconds, value);
            }

            @Override
            public Long decrBy(byte[] key, long integer) {
                return obj.decrBy(key, integer);
            }

            @Override
            public Long decr(byte[] key) {
                return obj.decr(key);
            }

            @Override
            public Long incrBy(byte[] key, long integer) {
                return obj.incrBy(key, integer);
            }

            @Override
            public Double incrByFloat(byte[] key, double value) {
                return obj.incrByFloat(key, value);
            }

            @Override
            public Long incr(byte[] key) {
                return obj.incr(key);
            }

            @Override
            public Long append(byte[] key, byte[] value) {
                return obj.append(key, value);
            }

            @Override
            public byte[] substr(byte[] key, int start, int end) {
                return obj.substr(key, start, end);
            }

            @Override
            public Long hset(byte[] key, byte[] field, byte[] value) {
                return obj.hset(key, field, value);
            }

            @Override
            public byte[] hget(byte[] key, byte[] field) {
                return obj.hget(key, field);
            }

            @Override
            public Long hsetnx(byte[] key, byte[] field, byte[] value) {
                return obj.hsetnx(key, field, value);
            }

            @Override
            public String hmset(byte[] key, Map<byte[], byte[]> hash) {
                return obj.hmset(key, hash);
            }

            @Override
            public List<byte[]> hmget(byte[] key, byte[]... fields) {
                return obj.hmget(key, fields);
            }

            @Override
            public Long hincrBy(byte[] key, byte[] field, long value) {
                return obj.hincrBy(key, field, value);
            }

            @Override
            public Double hincrByFloat(byte[] key, byte[] field, double value) {
                return obj.hincrByFloat(key, field, value);
            }

            @Override
            public Boolean hexists(byte[] key, byte[] field) {
                return obj.hexists(key, field);
            }

            @Override
            public Long hdel(byte[] key, byte[]... field) {
                return obj.hdel(key, field);
            }

            @Override
            public Long hlen(byte[] key) {
                return obj.hlen(key);
            }

            @Override
            public Set<byte[]> hkeys(byte[] key) {
                return obj.hkeys(key);
            }

            @Override
            public Collection<byte[]> hvals(byte[] key) {
                return obj.hvals(key);
            }

            @Override
            public Map<byte[], byte[]> hgetAll(byte[] key) {
                return obj.hgetAll(key);
            }

            @Override
            public Long rpush(byte[] key, byte[]... args) {
                return obj.rpush(key, args);
            }

            @Override
            public Long lpush(byte[] key, byte[]... args) {
                return obj.lpush(key, args);
            }

            @Override
            public Long llen(byte[] key) {
                return obj.llen(key);
            }

            @Override
            public List<byte[]> lrange(byte[] key, long start, long end) {
                return obj.lrange(key, start, end);
            }

            @Override
            public String ltrim(byte[] key, long start, long end) {
                return obj.ltrim(key, start, end);
            }

            @Override
            public byte[] lindex(byte[] key, long index) {
                return obj.lindex(key, index);
            }

            @Override
            public String lset(byte[] key, long index, byte[] value) {
                return obj.lset(key, index, value);
            }

            @Override
            public Long lrem(byte[] key, long count, byte[] value) {
                return obj.lrem(key, count, value);
            }

            @Override
            public byte[] lpop(byte[] key) {
                return obj.lpop(key);
            }

            @Override
            public byte[] rpop(byte[] key) {
                return obj.rpop(key);
            }

            @Override
            public Long sadd(byte[] key, byte[]... member) {
                return obj.sadd(key, member);
            }

            @Override
            public Set<byte[]> smembers(byte[] key) {
                return obj.smembers(key);
            }

            @Override
            public Long srem(byte[] key, byte[]... member) {
                return obj.srem(key, member);
            }

            @Override
            public byte[] spop(byte[] key) {
                return obj.spop(key);
            }

            @Override
            public Set<byte[]> spop(byte[] key, long count) {
                return obj.spop(key, count);
            }

            @Override
            public Long scard(byte[] key) {
                return obj.scard(key);
            }

            @Override
            public Boolean sismember(byte[] key, byte[] member) {
                return obj.sismember(key, member);
            }

            @Override
            public byte[] srandmember(byte[] key) {
                return obj.srandmember(key);
            }

            @Override
            public List<byte[]> srandmember(byte[] key, int count) {
                return obj.srandmember(key, count);
            }

            @Override
            public Long strlen(byte[] key) {
                return obj.strlen(key);
            }

            @Override
            public Long zadd(byte[] key, double score, byte[] member) {
                return obj.zadd(key, score, member);
            }

            @Override
            public Long zadd(byte[] key, double score, byte[] member, ZAddParams params) {
                return obj.zadd(key, score, member, params);
            }

            @Override
            public Long zadd(byte[] key, Map<byte[], Double> scoreMembers) {
                return obj.zadd(key, scoreMembers);
            }

            @Override
            public Long zadd(byte[] key, Map<byte[], Double> scoreMembers, ZAddParams params) {
                return obj.zadd(key, scoreMembers, params);
            }

            @Override
            public Set<byte[]> zrange(byte[] key, long start, long end) {
                return obj.zrange(key, start, end);
            }

            @Override
            public Long zrem(byte[] key, byte[]... member) {
                return obj.zrem(key, member);
            }

            @Override
            public Double zincrby(byte[] key, double score, byte[] member) {
                return obj.zincrby(key, score, member);
            }

            @Override
            public Double zincrby(byte[] key, double score, byte[] member, ZIncrByParams params) {
                return obj.zincrby(key, score, member, params);
            }

            @Override
            public Long zrank(byte[] key, byte[] member) {
                return obj.zrank(key, member);
            }

            @Override
            public Long zrevrank(byte[] key, byte[] member) {
                return obj.zrevrank(key, member);
            }

            @Override
            public Set<byte[]> zrevrange(byte[] key, long start, long end) {
                return obj.zrevrange(key, start, end);
            }

            @Override
            public Set<Tuple> zrangeWithScores(byte[] key, long start, long end) {
                return obj.zrangeWithScores(key, start, end);
            }

            @Override
            public Set<Tuple> zrevrangeWithScores(byte[] key, long start, long end) {
                return obj.zrevrangeWithScores(key, start, end);
            }

            @Override
            public Long zcard(byte[] key) {
                return obj.zcard(key);
            }

            @Override
            public Double zscore(byte[] key, byte[] member) {
                return obj.zscore(key, member);
            }

            @Override
            public List<byte[]> sort(byte[] key) {
                return obj.sort(key);
            }

            @Override
            public List<byte[]> sort(byte[] key, SortingParams sortingParameters) {
                return obj.sort(key, sortingParameters);
            }

            @Override
            public Long zcount(byte[] key, double min, double max) {
                return obj.zcount(key, min, max);
            }

            @Override
            public Long zcount(byte[] key, byte[] min, byte[] max) {
                return obj.zcount(key, min, max);
            }

            @Override
            public Set<byte[]> zrangeByScore(byte[] key, double min, double max) {
                return obj.zrangeByScore(key, min, max);
            }

            @Override
            public Set<byte[]> zrangeByScore(byte[] key, byte[] min, byte[] max) {
                return obj.zrangeByScore(key, min, max);
            }

            @Override
            public Set<byte[]> zrevrangeByScore(byte[] key, double max, double min) {
                return obj.zrevrangeByScore(key, min, max);
            }

            @Override
            public Set<byte[]> zrangeByScore(byte[] key, double min, double max, int offset, int count) {
                return obj.zrangeByScore(key, min, max, offset, count);
            }

            @Override
            public Set<byte[]> zrevrangeByScore(byte[] key, byte[] max, byte[] min) {
                return obj.zrevrangeByScore(key, max, min);
            }

            @Override
            public Set<byte[]> zrangeByScore(byte[] key, byte[] min, byte[] max, int offset, int count) {
                return obj.zrangeByScore(key, min, max, offset, count);
            }

            @Override
            public Set<byte[]> zrevrangeByScore(byte[] key, double max, double min, int offset, int count) {
                return obj.zrevrangeByScore(key, max, min, offset, count);
            }

            @Override
            public Set<Tuple> zrangeByScoreWithScores(byte[] key, double min, double max) {
                return obj.zrangeByScoreWithScores(key, min, max);
            }

            @Override
            public Set<Tuple> zrevrangeByScoreWithScores(byte[] key, double max, double min) {
                return obj.zrevrangeByScoreWithScores(key, max, min);
            }

            @Override
            public Set<Tuple> zrangeByScoreWithScores(byte[] key, double min, double max, int offset, int count) {
                return obj.zrangeByScoreWithScores(key, min, max, offset, count);
            }

            @Override
            public Set<byte[]> zrevrangeByScore(byte[] key, byte[] max, byte[] min, int offset, int count) {
                return obj.zrevrangeByScore(key, max, min, offset, count);
            }

            @Override
            public Set<Tuple> zrangeByScoreWithScores(byte[] key, byte[] min, byte[] max) {
                return obj.zrangeByScoreWithScores(key, min, max);
            }

            @Override
            public Set<Tuple> zrevrangeByScoreWithScores(byte[] key, byte[] max, byte[] min) {
                return obj.zrevrangeByScoreWithScores(key, max, min);
            }

            @Override
            public Set<Tuple> zrangeByScoreWithScores(byte[] key, byte[] min, byte[] max, int offset, int count) {
                return obj.zrangeByScoreWithScores(key, min, max, offset, count);
            }

            @Override
            public Set<Tuple> zrevrangeByScoreWithScores(byte[] key, double max, double min, int offset, int count) {
                return obj.zrevrangeByScoreWithScores(key, max, min, offset, count);
            }

            @Override
            public Set<Tuple> zrevrangeByScoreWithScores(byte[] key, byte[] max, byte[] min, int offset, int count) {
                return obj.zrevrangeByScoreWithScores(key, max, min, offset, count);
            }

            @Override
            public Long zremrangeByRank(byte[] key, long start, long end) {
                return obj.zremrangeByRank(key, start, end);
            }

            @Override
            public Long zremrangeByScore(byte[] key, double start, double end) {
                return obj.zremrangeByScore(key, start, end);
            }

            @Override
            public Long zremrangeByScore(byte[] key, byte[] start, byte[] end) {
                return obj.zremrangeByScore(key, start, end);
            }

            @Override
            public Long zlexcount(byte[] key, byte[] min, byte[] max) {
                return obj.zlexcount(key, min, max);
            }

            @Override
            public Set<byte[]> zrangeByLex(byte[] key, byte[] min, byte[] max) {
                return obj.zrangeByLex(key, min, max);
            }

            @Override
            public Set<byte[]> zrangeByLex(byte[] key, byte[] min, byte[] max, int offset, int count) {
                return obj.zrangeByLex(key, min, max, offset, count);
            }

            @Override
            public Set<byte[]> zrevrangeByLex(byte[] key, byte[] max, byte[] min) {
                return obj.zrevrangeByLex(key, max, min);
            }

            @Override
            public Set<byte[]> zrevrangeByLex(byte[] key, byte[] max, byte[] min, int offset, int count) {
                return obj.zrevrangeByLex(key, max, min, offset, count);
            }

            @Override
            public Long zremrangeByLex(byte[] key, byte[] min, byte[] max) {
                return obj.zremrangeByLex(key, min, max);
            }

            @Override
            public Long linsert(byte[] key, BinaryClient.LIST_POSITION where, byte[] pivot, byte[] value) {
                return obj.linsert(key, where, pivot, value);
            }

            @Override
            public Long lpushx(byte[] key, byte[]... arg) {
                return obj.lpushx(key, arg);
            }

            @Override
            public Long rpushx(byte[] key, byte[]... arg) {
                return obj.rpushx(key, arg);
            }

            @Override
            public List<byte[]> blpop(byte[] arg) {
                return null;
            }

            @Override
            public List<byte[]> brpop(byte[] arg) {
                return null;
            }

            @Override
            public Long del(byte[] key) {
                return obj.del(key);
            }

            @Override
            public byte[] echo(byte[] arg) {
                return obj.echo(arg);
            }

            @Override
            public Long move(byte[] key, int dbIndex) {
                return 0L;
            }

            @Override
            public Long bitcount(byte[] key) {
                return obj.bitcount(key);
            }

            @Override
            public Long bitcount(byte[] key, long start, long end) {
                return obj.bitcount(key, start, end);
            }

            @Override
            public Long pfadd(byte[] key, byte[]... elements) {
                return obj.pfadd(key, elements);
            }

            @Override
            public long pfcount(byte[] key) {
                return obj.pfcount(key);
            }

            @Override
            public Long geoadd(byte[] key, double longitude, double latitude, byte[] member) {
                return obj.geoadd(key, longitude, latitude, member);
            }

            @Override
            public Long geoadd(byte[] key, Map<byte[], GeoCoordinate> memberCoordinateMap) {
                return obj.geoadd(key, memberCoordinateMap);
            }

            @Override
            public Double geodist(byte[] key, byte[] member1, byte[] member2) {
                return obj.geodist(key, member1, member2);
            }

            @Override
            public Double geodist(byte[] key, byte[] member1, byte[] member2, GeoUnit unit) {
                return obj.geodist(key, member1, member2, unit);
            }

            @Override
            public List<byte[]> geohash(byte[] key, byte[]... members) {
                return obj.geohash(key, members);
            }

            @Override
            public List<GeoCoordinate> geopos(byte[] key, byte[]... members) {
                return obj.geopos(key, members);
            }

            @Override
            public List<GeoRadiusResponse> georadius(byte[] key, double longitude, double latitude, double radius, GeoUnit unit) {
                return obj.georadius(key, longitude, latitude, radius, unit);
            }

            @Override
            public List<GeoRadiusResponse> georadius(byte[] key, double longitude, double latitude, double radius, GeoUnit unit, GeoRadiusParam param) {
                return obj.georadius(key, longitude, latitude, radius, unit, param);
            }

            @Override
            public List<GeoRadiusResponse> georadiusByMember(byte[] key, byte[] member, double radius, GeoUnit unit) {
                return obj.georadiusByMember(key, member, radius, unit);
            }

            @Override
            public List<GeoRadiusResponse> georadiusByMember(byte[] key, byte[] member, double radius, GeoUnit unit, GeoRadiusParam param) {
                return obj.georadiusByMember(key, member, radius, unit, param);
            }

            @Override
            public ScanResult<Map.Entry<byte[], byte[]>> hscan(byte[] key, byte[] cursor) {
                return obj.hscan(key, cursor);
            }

            @Override
            public ScanResult<Map.Entry<byte[], byte[]>> hscan(byte[] key, byte[] cursor, ScanParams params) {
                return obj.hscan(key, cursor, params);
            }

            @Override
            public ScanResult<byte[]> sscan(byte[] key, byte[] cursor) {
                return obj.sscan(key, cursor);
            }

            @Override
            public ScanResult<byte[]> sscan(byte[] key, byte[] cursor, ScanParams params) {
                return obj.sscan(key, cursor, params);
            }

            @Override
            public ScanResult<Tuple> zscan(byte[] key, byte[] cursor) {
                return obj.zscan(key, cursor);
            }

            @Override
            public ScanResult<Tuple> zscan(byte[] key, byte[] cursor, ScanParams params) {
                return obj.zscan(key, cursor, params);
            }

            @Override
            public List<byte[]> bitfield(byte[] key, byte[]... arguments) {
                return obj.bitfield(key, arguments);
            }
        };
    }

}
