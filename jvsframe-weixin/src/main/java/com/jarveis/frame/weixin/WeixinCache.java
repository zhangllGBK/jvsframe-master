package com.jarveis.frame.weixin;

import com.jarveis.frame.cache.Puddle;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 微信缓存
 *
 * @author liuguojun
 * @since 2021-06-09
 */
public class WeixinCache {

    private static final Logger log = LoggerFactory.getLogger(WeixinCache.class);

    private static final String region = "weixin";
    private static long timestamp = 0L;

    /**
     * 配置文件时间比较并更新
     *
     * @param str 最新时间
     * @return boolean
     */
    public static boolean compareTimestamp(String str) {
        long temp = NumberUtils.toLong(str, 0L);
        if (timestamp < temp) {
            timestamp = temp;
            return true;
        } else {
            return false;
        }
    }

    /**
     * 设置常量
     *
     * @param key 键
     * @param value 值
     */
    public static void putConstant(String key, String value){
        if (log.isDebugEnabled()) {
            log.debug(String.format("constant name=%s, value=%s", key, value));
        }
        Puddle.getChannel().put(region, key, value);
    }

    /**
     * 设置常量
     *
     * @param key 键
     * @param value 值
     * @param expireInSecond 缓存有效期
     */
    public static void putConstant(String key, String value, int expireInSecond) {
        if (log.isDebugEnabled()) {
            log.debug(String.format("constant name=%s, value=%s, expire=%d", key, value, expireInSecond));
        }
        Puddle.getChannel().put(region, key, value, expireInSecond);
    }

    /**
     * 获取常量
     *
     * @param key 键
     * @return String
     */
    public static String getConstant(String key){
        return (String)Puddle.getChannel().get(region, key).getValue();
    }

    /**
     * 缓存关键字回复
     *
     * @param key 关键字
     * @param value 值
     */
    public static void putKeyword(String key, String value) {
        if (log.isDebugEnabled()) {
            log.debug(String.format("keyword name=%s, value=%s", key, value));
        }
        Puddle.getChannel().put(region, "keyword:"+key, value);
    }

    /**
     * 获取关键字回复
     *
     * @param key 关键字
     * @return String
     */
    public static String getKeyword(String key) {
        return (String)Puddle.getChannel().get(region, "keyword:"+key).getValue();
    }

}
