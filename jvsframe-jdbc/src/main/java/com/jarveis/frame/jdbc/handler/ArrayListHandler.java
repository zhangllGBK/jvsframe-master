package com.jarveis.frame.jdbc.handler;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;

import com.jarveis.frame.jdbc.ResultSetHandler;

/**
 * 数组对象结果集
 * 
 * @author liuguojun
 */
public class ArrayListHandler implements ResultSetHandler {

	public Object handle(ResultSet rs) throws SQLException {
		ArrayList<Object[]> result = new ArrayList<Object[]>();
		
		ResultSetMetaData meta = rs.getMetaData();
		int cols = meta.getColumnCount();
		while (rs.next()) {
			Object[] object = new Object[cols];
			for (int i = 0; i < cols; i++) {
				String label = meta.getColumnLabel(i + 1);
				int type = meta.getColumnType(i + 1);
				
				switch (type) {
				case Types.CHAR:
				case Types.VARCHAR:
				case Types.LONGVARCHAR:
					object[i] = rs.getString(label);
					break;
				case Types.TINYINT:	
				case Types.SMALLINT:
				case Types.INTEGER:
					object[i] = rs.getInt(label);
					break;
				case Types.BIGINT:
					object[i] = rs.getLong(label);
					break;
				case Types.FLOAT:
					object[i] = rs.getLong(label);
					break;
				case Types.REAL:
				case Types.DOUBLE:
					object[i] = rs.getDouble(label);
					break;
				case Types.DECIMAL:
				case Types.NUMERIC:
					object[i] = rs.getBigDecimal(label);
					break;
				case Types.DATE:
				case Types.TIME:
				case Types.TIMESTAMP:
					object[i] = rs.getTimestamp(label);
					break;
				default:
					object[i] = rs.getObject(label);
				}
			}
			
			result.add(object);
		}

		return result;
	}

}
