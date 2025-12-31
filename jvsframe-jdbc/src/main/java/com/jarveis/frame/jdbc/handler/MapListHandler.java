package com.jarveis.frame.jdbc.handler;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jarveis.frame.jdbc.ResultSetHandler;

/**
 * 数组对象结果集
 * 
 * @author liuguojun
 */
public class MapListHandler implements ResultSetHandler {

	public Object handle(ResultSet rs) throws SQLException {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		
		ResultSetMetaData meta = rs.getMetaData();
		int cols = meta.getColumnCount();
		while (rs.next()) {
			HashMap<String, Object> map = new HashMap<String, Object>(cols);
			for (int i = 0; i < cols; i++) {
				String label = meta.getColumnLabel(i + 1);
				int type = meta.getColumnType(i + 1);
				switch (type) {
				case Types.CHAR:
				case Types.VARCHAR:
				case Types.LONGVARCHAR:
					map.put(label, rs.getString(label));
					break;
				case Types.SMALLINT:
					map.put(label, rs.getShort(label));
					break;
				case Types.INTEGER:
					map.put(label, rs.getInt(label));
					break;
				case Types.BIGINT:
					map.put(label, rs.getLong(label));
					break;
				case Types.FLOAT:
					map.put(label, rs.getFloat(label));
					break;
				case Types.REAL:
				case Types.DOUBLE:
					map.put(label, rs.getDouble(label));
					break;
				case Types.DECIMAL:
				case Types.NUMERIC:
					map.put(label, rs.getBigDecimal(label));
					break;
				case Types.DATE:
				case Types.TIME:
				case Types.TIMESTAMP:
					map.put(label, rs.getTimestamp(label));
					break;
				default:
					map.put(label, rs.getObject(label));
				}
			}
			result.add(map);
		}
		
		return result;
	}

}
