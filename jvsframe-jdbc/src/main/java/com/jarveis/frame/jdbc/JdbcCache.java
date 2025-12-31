package com.jarveis.frame.jdbc;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import com.jarveis.frame.jdbc.ant.TableInfo;

/**
 * Jdbc配置类
 * 
 * @author liuguojun
 */
public class JdbcCache {

	private static ConcurrentHashMap<String, DataSourceWrapper> jdbcDataSource = new ConcurrentHashMap<String, DataSourceWrapper>();
	/**
	 * sql语句缓存
	 */
	private static HashMap<String, String> jdbcSQL = new HashMap<String, String>();
	/**
	 * 表结构缓存
	 */
	private static HashMap<String, TableInfo> jdbcTable = new HashMap<String, TableInfo>();

	/**
	 * 设置数据源对象缓存
	 * 
	 * @param dsw
	 */
	public static void putDataSource(DataSourceWrapper dsw) {
		jdbcDataSource.put(dsw.getId(), dsw);
	}

	/**
	 * 获取数据源对象缓存
	 * 
	 * @param key
	 * @return
	 */
	public static DataSourceWrapper getDataSource(String key) {
		return jdbcDataSource.get(key);
	}

	/**
	 * 设置Table对象缓存
	 * 
	 * @param key
	 * @param value
	 */
	public static void putTable(String key, TableInfo value) {
		jdbcTable.put(key, value);
	}

	/**
	 * 获取Table对象缓存
	 * 
	 * @param key
	 * @return
	 */
	public static TableInfo getTable(String key) {
		return jdbcTable.get(key);
	}

	/**
	 * 设置SQL语句缓存
	 * 
	 * @param key
	 * @param value
	 */
	public static void putSQL(String key, String value) {
		jdbcSQL.put(key, value);
	}

	/**
	 * 获取sql语名缓存
	 * 
	 * @param key
	 * @return
	 */
	public static String getSQL(String key) {
		return jdbcSQL.get(key);
	}

}
