package com.jarveis.frame.jdbc.parser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.jarveis.frame.jdbc.ant.ColumnInfo;
import com.jarveis.frame.jdbc.ant.TableInfo;

/**
 * 对象的插入语句解析类
 * 
 * @author liuguojun
 */
public class LoadByIdParser extends AbstractIdParser {

	public LoadByIdParser(Serializable id) {
		this.id = id;
	}

	public Object parse(Serializable s) throws Exception {
		List<Object> params = new ArrayList<Object>();
		StringBuffer columns = new StringBuffer();

		TableInfo tableInfo = getTableInfo(s);
		List<ColumnInfo> columnInfos = tableInfo.getColumnInfos(); // 字段信息
		for (int i = 0; i < columnInfos.size(); i++) {
			ColumnInfo columnInfo = columnInfos.get(i);
			String column = columnInfo.getColumn();
			String field = columnInfo.getField();
			columns.append(column).append(" as ").append(field);
			columns.append(", ");
		}
		StringBuffer sql = new StringBuffer("select ");
		sql.append(columns.substring(0, columns.length() - 2));
		sql.append(" from ").append(tableInfo.getTable()).append(" where ");
		sql.append(getCondition(tableInfo.getPrimaryKeys(), params));

		return new Object[] { sql.toString(), params.toArray() };
	}

}
