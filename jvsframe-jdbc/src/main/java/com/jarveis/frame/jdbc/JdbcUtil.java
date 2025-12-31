package com.jarveis.frame.jdbc;

import com.jarveis.frame.bean.BeanProperty;
import com.jarveis.frame.bean.BeanUtil;
import com.jarveis.frame.jdbc.ant.TableInfo;
import com.jarveis.frame.jdbc.handler.BeanHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 数据库连接工具类
 * 
 * @author liuguojun
 */
public final class JdbcUtil {

	private static final Logger log = LoggerFactory.getLogger(JdbcUtil.class);
	
	private static DataSourceWrapper dataSourceWrapper;
	private static final ThreadLocal<DataSourceWrapper> dsThreadLocal = new ThreadLocal<DataSourceWrapper>();
	private static final ThreadLocal<Connection> cnThreadLocal = new ThreadLocal<Connection>();

	/**
	 * 批量操作数据库
	 * 
	 * @param sql
	 * @param params
	 * @return int[]
	 * @throws SQLException
	 */
	public static int[] batch(String sql, Object[][] params)
			throws SQLException {
		return batch(getConnection(), sql, params);
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
	public static int[] batch(Connection conn, String sql, Object[][] params)
			throws SQLException {
		return new JdbcQuery(conn).batch(getSql(sql), params);
	}

	/**
	 * 批量操作数据库
	 * 
	 * @param sql
	 * @param params
	 * @return int[]
	 * @throws SQLException
	 */
	public static int[] batch(String sql, List params) throws SQLException {
		return batch(getConnection(), sql, params);
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
	public static int[] batch(Connection conn, String sql, List params)
			throws SQLException {
		return new JdbcQuery(conn).batch(getSql(sql), params);
	}

	/**
	 * 更新(插入、修改、删除)数据库
	 * 
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public static int excute(String sql, Object[] params) throws SQLException {
		return excute(getConnection(), sql, params);
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
	public static int excute(Connection conn, String sql, Object[] params)
			throws SQLException {
		return new JdbcQuery(conn).update(getSql(sql), params);
	}

	/**
	 * 查询数据库
	 * 
	 * @param sql
	 * @param rsh
	 * @return
	 * @throws SQLException
	 */
	public static Object query(String sql, ResultSetHandler rsh)
			throws SQLException {
		return query(getConnection(), getSql(sql), rsh);
	}

	/**
	 * 查询数据库
	 * 
	 * @param conn
	 * @param sql
	 * @param rsh
	 * @return Object
	 * @throws SQLException
	 */
	public static Object query(Connection conn, String sql, ResultSetHandler rsh)
			throws SQLException {
		return new JdbcQuery(conn).query(getSql(sql), rsh, null);
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
	public static Object query(Connection conn, String sql,
			ResultSetHandler rsh, Object[] params) throws SQLException {
		return new JdbcQuery(conn).query(getSql(sql), rsh, params);
	}

	/**
	 * 通过命名参数的SQL语句来查询数据库
	 * 
	 * @param sql
	 * @param rsh
	 * @param params
	 * @return Object
	 * @throws SQLException
	 */
	public static Object query(String sql, ResultSetHandler rsh, Map params)
			throws SQLException {
		Object result = query(getConnection(), sql, rsh, params);
		return result;
	}

	/**
	 * 通过命名参数的SQL语句来查询数据库
	 * 
	 * @param conn
	 * @param sql
	 * @param rsh
	 * @param params
	 * @return Object
	 * @throws SQLException
	 */
	public static Object query(Connection conn, String sql,
			ResultSetHandler rsh, Map params) throws SQLException {
		if (params == null || params.isEmpty()) {	
			return query(conn, sql, rsh);
		} else {
			Object[] results = SqlParserFactory.query(getSql(sql), params);
			return query(conn, (String) results[0], rsh, (Object[]) results[1]);
		}
	}

	/**
	 * 通过对象来查询数据库
	 * 
	 * @param s
	 * @param rsh
	 * @return Object
	 * @throws SQLException
	 */
	public static Object query(Serializable s, ResultSetHandler rsh)
			throws SQLException {
		Object result = query(getConnection(), s, rsh);
		return result;
	}

	/**
	 * 通过对象来查询数据库
	 * 
	 * @param conn
	 * @param s
	 * @param rsh
	 * @return Object
	 * @throws SQLException
	 */
	public static Object query(Connection conn, Serializable s,
			ResultSetHandler rsh) throws SQLException {
		Object[] results = SqlParserFactory.query(s);
		return query(conn, (String) results[0], rsh, (Object[]) results[1]);
	}

	/**
	 * 加载对象
	 * 
	 * @param type
	 * @param id
	 * @return Object
	 * @throws SQLException
	 */
	public static Object load(Class type, Serializable id) throws SQLException {
		Object result = load(getConnection(), type, id);
		return result;
	}

	/**
	 * 加载对象
	 * 
	 * @param conn
	 * @param type
	 * @param id
	 * @return Object
	 * @throws SQLException
	 */
	public static Object load(Connection conn, Class type, Serializable id)
			throws SQLException {
		Object[] results = SqlParserFactory.loadById(type, id);
		return query(conn, (String) results[0], new BeanHandler(type),
				(Object[]) results[1]);
	}

	/**
	 * 加载对象
	 * 
	 * @param conn 数据库连接对象
	 * @param sql
	 * @param type
	 * @param id
	 * @return Object
	 * @throws SQLException
	 */
	public static Object load(Connection conn, String sql, Class type,
			Serializable id) throws SQLException {
		Map params = BeanProperty.toMap(id);
		Object[] results = SqlParserFactory.query(getSql(sql), params);
		return query(conn, (String) results[0], new BeanHandler(type),
				(Object[]) results[1]);
	}

	/**
	 * 保存对象
	 * 
	 * @param s 实体对象
	 * @return int
	 * @throws SQLException
	 */
	public static int save(Serializable s) throws SQLException {
		int result = save(getConnection(), s);
		return result;
	}

	/**
	 * 保存对象
	 * 
	 * @param conn 数据库连接对象
	 * @param s 实体对象
	 * @return int
	 * @throws SQLException
	 */
	public static int save(Connection conn, Serializable s) throws SQLException {
		Object[] results = SqlParserFactory.insert(s);
		return excute(conn, (String) results[0], (Object[]) results[1]);
	}

	/**
	 * 更新对象
	 * 
	 * @param s 实体对象
	 * @param id 主键
	 * @return
	 * @throws SQLException
	 */
	public static int update(Serializable s, Serializable id)
			throws SQLException {
		int result = update(getConnection(), s, id);
		return result;
	}

	/**
	 * 更新对象
	 * 
	 * @param conn
	 *            数据库连接对象
	 * @param s
	 *            实体对象
	 * @param id
	 *            主键
	 * @return int
	 * @throws SQLException
	 */
	public static int update(Connection conn, Serializable s, Serializable id)
			throws SQLException {
		Object[] results = SqlParserFactory.updateById(s, id);
		return excute(conn, (String) results[0], (Object[]) results[1]);
	}

	/**
	 * 删除对象
	 * 
	 * @param type
	 *            实体类型
	 * @param id
	 *            主键
	 * @return int
	 * @throws SQLException
	 */
	public static int delete(Class type, Serializable id) throws SQLException {
		int result = delete(getConnection(), type, id);
		return result;
	}

	/**
	 * 删除对象
	 * 
	 * @param conn
	 *            数据库连接对象
	 * @param type
	 *            实体类型
	 * @param id
	 *            主键
	 * @return int
	 * @throws SQLException
	 */
	public static int delete(Connection conn, Class type, Serializable id)
			throws SQLException {
		Object[] results = SqlParserFactory.deleteById(type, id);
		return excute(conn, (String) results[0], (Object[]) results[1]);
	}

	/**
	 * 获取当前事务模式
	 * 
	 * @return boolean
	 * @throws SQLException
	 */
	public static boolean getAutoCommit() throws SQLException {
		return getConnection().getAutoCommit();
	}

	/**
	 * 当前数据操作开启事务
	 * 
	 * @throws SQLException
	 */
	public static void startTransaction() throws SQLException {
		getConnection().setAutoCommit(false);
	}

	/**
	 * 提交事务
	 * 
	 * @throws SQLException
	 */
	public static void commit() throws SQLException {
		Connection conn = getConnection();
		if (conn != null && !conn.isClosed()) {
			conn.commit();
		}
	}

	/**
	 * 事务回滚
	 * 
	 * @throws SQLException
	 */
	public static void rollback() throws SQLException {
		Connection conn = getConnection();
		if (conn != null && !conn.isClosed()) {
			conn.rollback();
		}
	}

	/**
	 * 获取Sql缓存
	 * 
	 * @param sql
	 * @return String
	 */
	private static String getSql(String sql) {
		if (sql.trim().length() > 0) {
			String str = JdbcCache.getSQL(sql);
			if (StringUtils.isNotEmpty(str)) {
				sql = str;
			}
		}
		return sql;
	}

	/**
	 * 获取实体类的表名称
	 * 
	 * @param s
	 * @return
	 */
	public static String getClassTable(Serializable s) {
		String name = BeanUtil.getFname(s);
		TableInfo tableInfo = JdbcCache.getTable(name);

		if (tableInfo == null) {
			tableInfo = new TableInfo(s);
			JdbcCache.putTable(name, tableInfo);
		}

		return tableInfo.getTable();
	}
	
	/**
	 * 设置数据源
	 * 
	 * @param id
	 * @param ds
	 * @param def
	 */
	public static void setDataSource(String id, DataSource ds, boolean def) {

		DataSourceWrapper dsw = new DataSourceWrapper();
		dsw.setId(id);
		dsw.setDf(def);
		dsw.setDataSource(ds);

		if (dsw.getDf()) {
			dataSourceWrapper = dsw;
		}

		JdbcCache.putDataSource(dsw);
	}

	/**
	 * 返回当前线程使用的数据源
	 * 
	 * @return DataSource
	 * @throws Exception
	 */
	public static synchronized DataSource getDataSource() throws Exception {
		DataSourceWrapper dsw = dsThreadLocal.get();
		if (dsw == null) {
			if (dataSourceWrapper != null) {
				dsw = dataSourceWrapper;
				dsThreadLocal.set(dsw);
			} else {
				Exception ex = new Exception("数据源不能被发现, 请检查相关配置或是否初始化配置");
				log.error(ex.getMessage(), ex);
				throw ex;
			}
		}
		return (DataSource) dsw.getDataSource();
	}

	/**
	 * 改变当前线程使用的数据源
	 * 
	 * @param key
	 * @return DataSource
	 * @throws SQLException
	 */
	public static synchronized void changeDataSource(String key)
			throws SQLException {
		DataSourceWrapper dsw = JdbcCache.getDataSource(key);
		if (dsw == null) {
			throw new SQLException(key + "数据源不能够被发现");
		}
		dsThreadLocal.set(dsw);
	}

	/**
	 * 获取数据库连接
	 * 
	 * @return Connection
	 */
	public static synchronized Connection getConnection() {
		Connection conn = null;

		try {
			conn = cnThreadLocal.get();
			if (conn == null) {
				conn = getDataSource().getConnection();
				cnThreadLocal.set(conn);
			}
		} catch (Exception ex) {
			log.error("获取数据库连接出错", ex);
		}

		return conn;
	}

	/**
	 * 关闭数据库连接
	 * 
	 * @return
	 */
	public static synchronized void closeConnection() {
		Connection conn = cnThreadLocal.get();

		try {
			if (conn != null && !conn.isClosed()) {
				conn.close();
				cnThreadLocal.remove();
			}
		} catch (Exception ex) {
			log.error("获取数据库连接出错", ex);
		}
	}
}
