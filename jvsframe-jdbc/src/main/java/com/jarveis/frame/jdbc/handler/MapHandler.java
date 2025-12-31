package com.jarveis.frame.jdbc.handler;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;

import com.jarveis.frame.jdbc.ResultSetHandler;

/**
 * 数组对象结果集
 * 
 * @author liuguojun
 */
public class MapHandler implements ResultSetHandler {

	public Object handle(ResultSet rs) throws SQLException {
		HashMap<String, Object> result = null;

		if (rs.next()) {
			ResultSetMetaData meta = rs.getMetaData();
			int cols = meta.getColumnCount();
			result = new HashMap<String, Object>(cols);
			for (int i = 0; i < cols; i++) {
				String label = meta.getColumnLabel(i + 1);
				int type = meta.getColumnType(i + 1);
				switch (type) {
				case Types.CHAR:
				case Types.VARCHAR:
				case Types.LONGVARCHAR:
					result.put(label, rs.getString(label));
					break;
				case Types.SMALLINT:
					result.put(label, rs.getShort(label));
					break;
				case Types.INTEGER:
					result.put(label, rs.getInt(label));
					break;
				case Types.BIGINT:
					result.put(label, rs.getLong(label));
					break;
				case Types.FLOAT:
					result.put(label, rs.getFloat(label));
					break;
				case Types.REAL:
				case Types.DOUBLE:
					result.put(label, rs.getDouble(label));
					break;
				case Types.DECIMAL:
				case Types.NUMERIC:
					result.put(label, rs.getBigDecimal(label));
					break;
				case Types.DATE:
				case Types.TIME:
				case Types.TIMESTAMP:
					result.put(label, rs.getTimestamp(label));
					break;
				default:
					result.put(label, rs.getObject(label));
				}
			}
		}

		return result;
	}

}
