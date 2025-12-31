package com.jarveis.frame.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 结果集处理接口
 * 
 * @author liuguojun
 */
public interface ResultSetHandler {

	/**
	 * 结果集处理方法
	 * 
	 * @param rs
	 * @return Object
	 * @throws Exception
	 */
	public Object handle(ResultSet rs) throws SQLException;
}
