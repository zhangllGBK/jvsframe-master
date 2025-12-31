package com.jarveis.frame.jdbc.handler;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

import com.jarveis.frame.bean.BeanUtil;
import com.jarveis.frame.jdbc.JdbcCache;
import com.jarveis.frame.jdbc.ResultSetHandler;
import com.jarveis.frame.jdbc.ant.ColumnInfo;
import com.jarveis.frame.jdbc.ant.TableInfo;

/**
 * 数组对象结果集
 * 
 * @author liuguojun
 */
public class BeanHandler implements ResultSetHandler {

	private Class type;

	public BeanHandler(Class type) {
		this.type = type;
	}

	public Object handle(ResultSet rs) throws SQLException {
		Serializable bean = null;

		if (rs.next()) {
			TableInfo tableInfo = getTableInfo();
			ResultSetMetaData meta = rs.getMetaData();
			int cols = meta.getColumnCount();
			bean = (Serializable) BeanUtil.newInstance(type);
			for (int i = 0; i < cols; i++) {
				String label = meta.getColumnLabel(i + 1);
				int type = meta.getColumnType(i + 1);
				ColumnInfo columnInfo = tableInfo.getColumnInfo(label);
				
				switch (type) {
				case Types.CHAR:
				case Types.VARCHAR:
				case Types.LONGVARCHAR:
					if (columnInfo != null) {
						BeanUtil.setFieldValue(bean, columnInfo.getField(), rs.getString(label));
					} else {
						BeanUtil.setFieldValue(bean, label, rs.getString(label));
					}
					break;
				case Types.TINYINT:	
				case Types.SMALLINT:
				case Types.INTEGER:
					if (columnInfo != null) {
						BeanUtil.setFieldValue(bean, columnInfo.getField(), rs.getInt(label));
					} else {
						BeanUtil.setFieldValue(bean, label, rs.getInt(label));
					}
					break;
				case Types.BIGINT:
					if (columnInfo != null) {
						BeanUtil.setFieldValue(bean, columnInfo.getField(), rs.getLong(label));
					} else {
						BeanUtil.setFieldValue(bean, label, rs.getLong(label));
					}
					break;
				case Types.FLOAT:
					if (columnInfo != null) {
						BeanUtil.setFieldValue(bean, columnInfo.getField(), rs.getFloat(label));
					} else {
						BeanUtil.setFieldValue(bean, label, rs.getLong(label));
					}
					break;
				case Types.REAL:
				case Types.DOUBLE:
					if (columnInfo != null) {
						BeanUtil.setFieldValue(bean, columnInfo.getField(), rs.getDouble(label));
					} else {
						BeanUtil.setFieldValue(bean, label, rs.getDouble(label));
					}
					break;
				case Types.DECIMAL:
				case Types.NUMERIC:
					if (columnInfo != null) {
						BeanUtil.setFieldValue(bean, columnInfo.getField(), rs.getBigDecimal(label));
					} else {
						BeanUtil.setFieldValue(bean, label, rs.getBigDecimal(label));
					}
					break;
				case Types.DATE:
				case Types.TIME:
				case Types.TIMESTAMP:
					if (columnInfo != null) {
						BeanUtil.setFieldValue(bean, columnInfo.getField(), rs.getTimestamp(label));
					} else {
						BeanUtil.setFieldValue(bean, label, rs.getTimestamp(label));
					}
					break;
				default:
					if (columnInfo != null) {
						BeanUtil.setFieldValue(bean, columnInfo.getField(), rs.getObject(label));
					} else {
						BeanUtil.setFieldValue(bean, label, rs.getObject(label));
					}
				}
			}
		}

		return bean;
	}
	
	/**
	 * 获取表信息
	 * 
	 * @return TableInfo
	 */
	private TableInfo getTableInfo() {
		String name = type.getName();
		TableInfo tableInfo = JdbcCache.getTable(name);
		if (tableInfo == null) {
			tableInfo = new TableInfo(type);
			JdbcCache.putTable(name, tableInfo);
		}
		
		return tableInfo;
	}

}
