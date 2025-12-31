package com.jarveis.frame.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Jdbc查询类
 * 
 * @author liuguojun
 */
public class JdbcQuery {

	private static final Logger log = LoggerFactory.getLogger(JdbcQuery.class);

	private Connection connection;

	public JdbcQuery(Connection connection) {
		this.connection = connection;
	}

	/**
	 * 批量操作数据库
	 * 
	 * @param sql
	 * @param params
	 * @return int[]
	 * @throws SQLException
	 */
	public int[] batch(String sql, Object[][] params) throws SQLException {
		return batch(connection, sql, params);
	}

	/**
	 * 批量操作数据库
	 * 
	 * @param conn
	 * @param sql
	 * @param params
	 * @return int[]
	 * @throws SQLException
	 */
	public int[] batch(Connection conn, String sql, Object[][] params)
			throws SQLException {
		PreparedStatement stmt = null;
		int[] rows = null;
		try {
			if (log.isDebugEnabled()) {
				log.debug(sql);
			}
			stmt = conn.prepareStatement(sql);
			for (int i = 0; i < params.length; i++) {
				for (int j = 0; j < params[i].length; j++) {
					stmt.setObject(j + 1, params[i][j]);
				}
				stmt.addBatch();
			}
			rows = stmt.executeBatch();
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}

		return rows;
	}

	/**
	 * 批量操作数据库
	 * 
	 * @param sql
	 * @param params
	 * @return int[]
	 * @throws SQLException
	 */
	public int[] batch(String sql, List params) throws SQLException {
		return batch(connection, sql, params);
	}

	/**
	 * 批量操作数据库
	 * 
	 * @param conn
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public int[] batch(Connection conn, String sql, List params)
			throws SQLException {
		PreparedStatement stmt = null;
		int[] rows = null;
		try {
			if (log.isDebugEnabled()) {
				log.debug(sql);
			}
			stmt = conn.prepareStatement(sql);
			for (int i = 0; i < params.size(); i++) {
				Object[] arr = (Object[]) params.get(i);

				for (int j = 0; j < arr.length; j++) {
					stmt.setObject(j + 1, arr[j]);
				}
				stmt.addBatch();
			}
			rows = stmt.executeBatch();
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}

		return rows;
	}

	/**
	 * 查询数据库
	 * 
	 * @param sql
	 * @param rsh
	 * @param params
	 * @return Object
	 * @throws SQLException
	 */
	public Object query(String sql, ResultSetHandler rsh, Object[] params)
			throws SQLException {
		return query(connection, sql, rsh, params);
	}

	/**
	 * 查询数据库
	 * 
	 * @param conn
	 * @param sql
	 * @param rsh
	 * @param params
	 * @return Object
	 * @throws SQLException
	 */
	public Object query(Connection conn, String sql, ResultSetHandler rsh,
			Object[] params) throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Object result = null;

		try {
			if (log.isDebugEnabled()) {
				log.debug(sql);
			}
			stmt = conn.prepareStatement(sql);
			if (params != null) {
				for (int i = 0; i < params.length; i++) {
					stmt.setObject(i + 1, params[i]);
				}
			}
			rs = stmt.executeQuery();
			result = rsh.handle(rs);
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}
		}

		return result;
	}

	/**
	 * 更新(插入、修改、删除)数据库
	 * 
	 * @param sql
	 * @param params
	 * @return int
	 * @throws SQLException
	 */
	public int update(String sql, Object[] params) throws SQLException {
		return update(connection, sql, params);
	}

	/**
	 * 更新(插入、修改、删除)数据库
	 * 
	 * @param conn
	 * @param sql
	 * @param params
	 * @return int
	 * @throws SQLException
	 */
	public int update(Connection conn, String sql, Object[] params)
			throws SQLException {
		PreparedStatement stmt = null;
		int rows = 0;

		try {
			if (log.isDebugEnabled()) {
				log.debug(sql);
			}
			stmt = conn.prepareStatement(sql);
			for (int i = 0; i < params.length; i++) {
				stmt.setObject(i + 1, params[i]);
			}
			rows = stmt.executeUpdate();
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}

		return rows;
	}
}
