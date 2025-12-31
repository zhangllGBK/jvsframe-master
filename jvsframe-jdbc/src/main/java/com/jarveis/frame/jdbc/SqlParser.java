package com.jarveis.frame.jdbc;

import java.io.Serializable;

/**
 * Sql语句解析接口
 * 
 * @author liuguojun
 */
public interface SqlParser {

	/**
	 * Sql语句解析方法
	 * 
	 * @param s
	 * @return Object
	 * @throws Exception
	 */
	public Object parse(Serializable s) throws Exception;
}
