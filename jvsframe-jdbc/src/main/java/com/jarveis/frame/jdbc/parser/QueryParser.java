package com.jarveis.frame.jdbc.parser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.jarveis.frame.bean.BeanUtil;
import com.jarveis.frame.jdbc.ant.ColumnInfo;
import com.jarveis.frame.jdbc.ant.TableInfo;

/**
 * 对象的查询语句解析类
 * 
 * @author liuguojun
 */
public class QueryParser extends AbstractParser {

	public Object parse(Serializable s) throws Exception {
		List<Object> params = new ArrayList<Object>();
		StringBuilder columns = new StringBuilder();
		StringBuilder conditions = new StringBuilder();

		TableInfo tableInfo = getTableInfo(s);
		List<ColumnInfo> columnInfos = tableInfo.getColumnInfos(); // 字段信息
		for (int i = 0; i < columnInfos.size(); i++) {
			ColumnInfo columnInfo = columnInfos.get(i);
			String column = columnInfo.getColumn();
			String field = columnInfo.getField();
			columns.append(column).append(" as ").append(field);
			columns.append(", ");

			Object value = BeanUtil.getFieldValue(s, field);
			if (value == null) {
				continue;
			}
			conditions.append(column).append("=? and ");
			params.add(value);
		}
		StringBuilder sql = new StringBuilder("select ");
		sql.append(columns.substring(0, columns.length() - 2));
		sql.append(" from ").append(tableInfo.getTable()).append(" where ");
		sql.append(conditions.substring(0, conditions.length() - 5));

		return new Object[] { sql.toString(), params.toArray() };
	}

}
