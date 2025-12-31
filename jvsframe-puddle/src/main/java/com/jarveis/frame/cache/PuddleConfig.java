package com.jarveis.frame.cache;

import java.util.Properties;

/**
 * Puddle配置类
 * 
 * @author liuguojun
 * @since 2018-08-31
 */
public class PuddleConfig {

	private static Properties cacheConfig;
	private static Properties redisConfig;
	private static Properties jvsCacheConfig;

	public static Properties getCacheConfig() {
		return cacheConfig;
	}

	public static void setCacheConfig(Properties config) {
		cacheConfig = config;
	}

	public static Properties getRedisConfig() {
		return redisConfig;
	}

	public static void setRedisConfig(Properties config) {
		redisConfig = config;
	}

	public static Properties getJvsCacheConfig() {
		return jvsCacheConfig;
	}

	public static void setJvsCacheConfig(Properties jvsCacheConfig) {
		PuddleConfig.jvsCacheConfig = jvsCacheConfig;
	}
}
