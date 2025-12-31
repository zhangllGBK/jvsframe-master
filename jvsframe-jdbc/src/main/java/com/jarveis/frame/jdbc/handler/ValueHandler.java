package com.jarveis.frame.jdbc.handler;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

import com.jarveis.frame.jdbc.ResultSetHandler;

/**
 * 单个值对象
 * 
 * @author liuguojun
 */
public class ValueHandler implements ResultSetHandler {

	public Object handle(ResultSet rs) throws SQLException {

		Object result = null;
		ResultSetMetaData meta = rs.getMetaData();
		int cols = meta.getColumnCount();
		if (rs.next()) {
			for (int i = 0; i < cols; i++) {
				String label = meta.getColumnLabel(i + 1);
				int type = meta.getColumnType(i + 1);
				
				switch (type) {
				case Types.CHAR:
				case Types.VARCHAR:
				case Types.LONGVARCHAR:
					result = rs.getString(label);
					break;
				case Types.TINYINT:	
				case Types.SMALLINT:
				case Types.INTEGER:
					result = rs.getInt(label);
					break;
				case Types.BIGINT:
					result = rs.getLong(label);
					break;
				case Types.FLOAT:
					result = rs.getFloat(label);
					break;
				case Types.REAL:
				case Types.DOUBLE:
					result = rs.getDouble(label);
					break;
				case Types.DECIMAL:
				case Types.NUMERIC:
					result = rs.getBigDecimal(label);
					break;
				case Types.DATE:
				case Types.TIME:
				case Types.TIMESTAMP:
					result = rs.getTimestamp(label);
					break;
				default:
					result = rs.getObject(label);
				}
			}
		}

		return result;
	}

}
