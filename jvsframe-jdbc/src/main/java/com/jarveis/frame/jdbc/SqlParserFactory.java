package com.jarveis.frame.jdbc;

import com.jarveis.frame.jdbc.parser.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Map;

/**
 * sql解析器工厂类
 * 
 * @author liuguojun
 */
public class SqlParserFactory {

	private static final Logger log = LoggerFactory.getLogger(SqlParserFactory.class);
	
	/**
	 * 获取对象的插入语句
	 * 
	 * @param s
	 * @return Object[]
	 */
	public static Object[] insert(Serializable s){
		try {
			return (Object[])(new InsertParser().parse(s));
		} catch (Exception ex) {
			log.error("解析对象的保存SQL语句出错", ex);
			return null;
		}
	}
	
	/**
	 * 获取对象的更新语句
	 * 
	 * @param s
	 * @param id
	 * @return Object[]
	 */
	public static Object[] updateById(Serializable s, Serializable id) {
		try {
			return (Object[])(new UpdateByIdParser(id).parse(s));
		} catch (Exception ex) {
			log.error("解析对象的更新SQL语句出错", ex);
			return null;
		}
	}
	
	/**
	 * 获取对象的删除语句
	 * 
	 * @param s
	 * @param id
	 * @return Object[]
	 */
	public static Object[] deleteById(Class s, Serializable id) {
		try {
			return (Object[])(new DeleteByIdParser(id).parse(s));
		} catch (Exception ex) {
			log.error("解析对象的删除SQL语句出错", ex);
			return null;
		}
	}
	
	/**
	 * 获取基于对象主键的查询语句
	 * 
	 * @param s
	 * @param id
	 * @return Object[]
	 */
	public static Object[] loadById(Class s, Serializable id) {
		try {
			return (Object[])(new LoadByIdParser(id).parse(s));
		} catch (Exception ex) {
			log.error("解析对象的加载SQL语句出错", ex);
			return null;
		}
	}
	
	/**
	 * 获取对象的查询语句
	 * 
	 * @param s
	 * @return Object[]
	 */
	public static Object[] query(Serializable s) {
		try {
			return (Object[])(new QueryParser().parse(s));
		} catch (Exception ex) {
			log.error("解析对象的查询SQL语句出错", ex);
			return null;
		}
	}
	
	/**
	 * 解析基于名称参数的查询语句
	 * 
	 * @param sql
	 * @param params
	 * @return Object[]
	 */
	public static Object[] query(String sql, Map params) {
		try {
			return (Object[])(new QueryByNameParser(params).parse(sql));
		} catch (Exception ex) {
			log.error("解析SQL语句", ex);
			return null;
		}
	}
}
