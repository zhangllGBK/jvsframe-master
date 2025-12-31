package com.jarveis.frame.jdbc.handler;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;

import com.jarveis.frame.jdbc.ResultSetHandler;

/**
 * 单个值的结果集
 * 
 * @author liuguojun
 */
public class ValueListHandler implements ResultSetHandler {

	public Object handle(ResultSet rs) throws SQLException {
		ArrayList<Object> result = new ArrayList<Object>();
		
		ResultSetMetaData meta = rs.getMetaData();
		int cols = meta.getColumnCount();
		while (rs.next()) {
			Object object = null;
			
			for (int i = 0; i < cols; i++) {
				String label = meta.getColumnLabel(i + 1);
				int type = meta.getColumnType(i + 1);
				
				switch (type) {
				case Types.CHAR:
				case Types.VARCHAR:
				case Types.LONGVARCHAR:
					object = rs.getString(label);
					break;
				case Types.TINYINT:	
				case Types.SMALLINT:
				case Types.INTEGER:
					object = rs.getInt(label);
					break;
				case Types.BIGINT:
					object = rs.getLong(label);
					break;
				case Types.FLOAT:
					object = rs.getFloat(label);
					break;
				case Types.REAL:
				case Types.DOUBLE:
					object = rs.getDouble(label);
					break;
				case Types.DECIMAL:
				case Types.NUMERIC:
					object = rs.getBigDecimal(label);
					break;
				case Types.DATE:
				case Types.TIME:
				case Types.TIMESTAMP:
					object = rs.getTimestamp(label);
					break;
				default:
					object = rs.getObject(label);
				}
			}
			
			result.add(object);
		}

		return result;
	}

}
