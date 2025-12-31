package com.jarveis.frame.jdbc.parser;


/**
 * 数据查询语言(select)
 * 
 * @author liuguojun
 */
public class DataQuery {

//	private static final Log logger = LogFactory.getLog(DataQuery.class);
//	private Connection connection; // 数据库连接对象
//	private PreparedStatement ps; // SQL语句操作对象
//	private String sql;
//	private String[] paramKeys;
//	private Map<Integer, Object> parameters;
//	private int firstResult = 0; // 第一条记录
//	private int maxResults = 0; // 最大记录
//	private boolean supportLimit = false; // 支持分页
//
//	public DataQuery(Connection connection, String sql) throws SQLException {
//		this.connection = connection;
//		this.sql = sql;
//		this.paramKeys = getParamKeys();
//		this.parameters = new HashMap<Integer, Object>();
//	}
//
//	/**
//	 * 获取参数键的集合
//	 * 
//	 * @return String[]
//	 */
//	private String[] getParamKeys() {
//		String[] keys = new String[10];
//		String s = sql;
//		if (sql.indexOf("?") > -1) {
//			int i, key = 0;
//			while ((i = s.indexOf("?")) > -1) {
//				s = s.substring(i + 1);
//				keys[key++] = String.valueOf(key);
//			}
//		} else if (sql.indexOf(":") > -1) {
//			int i, count = 0;
//			while ((i = s.indexOf(":")) > -1) {
//				s = s.substring(i + 1);
//				i = s.indexOf(" ");
//				String key = (i > -1) ? s.substring(0, i) : s;
//				sql = sql.replaceAll(":" + key, "?");
//				keys[count++] = key;
//			}
//		}
//		return keys;
//	}
//
//	/**
//	 * 设置查找时的参数
//	 * 
//	 * @param key
//	 * @param value
//	 * @throws SQLException
//	 */
//	public void setProperty(int key, Object value) throws SQLException {
//		int k = Integer.parseInt(paramKeys[key - 1]);
//		if (key == k) {
//			parameters.put(key, value);
//		}
//	}
//
//	/**
//	 * 设置查找时的参数
//	 * 
//	 * @param key
//	 * @param value
//	 * @throws SQLException
//	 */
//	public void setProperty(String key, Object value) throws SQLException {
//		for (int i = 0; i < paramKeys.length; i++) {
//			if (key.equals(paramKeys[i])) {
//				parameters.put(i + 1, value);
//			}
//		}
//	}
//
//	/**
//	 * 加载参数
//	 * 
//	 * @throws SQLException
//	 */
//	private void loadParameters() throws SQLException {
//		Set<Integer> names = parameters.keySet();
//		int count = 1;
//		for (Iterator<Integer> it = names.iterator(); it.hasNext(); count++) {
//			Integer key = it.next();
//			Object value = parameters.get(key);
//			if (value instanceof Integer) {
//				ps.setInt(key, (Integer) value);
//			} else if (value instanceof String) {
//				ps.setString(key, (String) value);
//			} else if (value instanceof java.sql.Date) {
//				ps.setDate(key, (java.sql.Date) value);
//			} else {
//				ps.setObject(key, value);
//			}
//		}
//		if (supportLimit) {
//			ps.setInt(count, firstResult);
//			ps.setInt(count + 1, maxResults);
//		}
//	}
//
//	/**
//	 * 设置开始记录
//	 * 
//	 * @param first
//	 */
//	public void setFirstResult(int first) {
//		this.firstResult = first;
//	}
//
//	/**
//	 * 设置最大的记录数
//	 * 
//	 * @param max
//	 */
//	public void setMaxResults(int max) {
//		this.maxResults = max;
//	}
//
//	/**
//	 * 构建分页语句
//	 */
//	private void buildLimit() {
//		if (firstResult != 0 || maxResults != 0) {
//			sql = sql + " limit ?, ?";
//			supportLimit = true;
//		}
//	}
//
//	/**
//	 * 获取记录数
//	 * 
//	 * @return int
//	 * @throws SQLException
//	 */
//	public int getResultCount() throws SQLException {
//		int formIndex = sql.indexOf(" from ");
//		if (formIndex == -1) {
//			formIndex = sql.indexOf(" FROM ");
//		}
//		if (formIndex == -1) {
//			throw new SQLException(sql + " the syntax error");
//		}
//		StringBuffer csql = new StringBuffer("select count(*) as count");
//		csql.append(sql.substring(formIndex));
//		ps = connection.prepareStatement(csql.toString());
//		loadParameters();
//		ResultSet rs = ps.executeQuery();
//		rs.next();
//
//		return rs.getInt("count") < 1 ? 1 : rs.getInt("count");
//	}
//
//	/**
//	 * 获取查询的结果集
//	 * 
//	 * @return List
//	 * @throws SQLException
//	 */
//	public List<Map<String, Object>> list() throws SQLException {
//		buildLimit();
//
//
//		ps = connection.prepareStatement(sql);
//		loadParameters();
//		ResultSet rs = ps.executeQuery();
//
//		return PreparedUtil.getResult(rs);
//	}
//
//	/**
//	 * 获取查询的结果集
//	 * 
//	 * @param type
//	 * @return List
//	 * @throws SQLException
//	 */
//	public List<Serializable> list(Class<? extends Serializable> type)
//			throws SQLException {
//		return PreparedUtil.convertResult(list(), type);
//	}
}
