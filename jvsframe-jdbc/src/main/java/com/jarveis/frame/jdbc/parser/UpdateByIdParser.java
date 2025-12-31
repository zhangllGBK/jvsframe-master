package com.jarveis.frame.jdbc.parser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.jarveis.frame.bean.BeanUtil;
import com.jarveis.frame.jdbc.ant.ColumnInfo;
import com.jarveis.frame.jdbc.ant.TableInfo;

/**
 * 对象的插入语句解析类
 * 
 * @author liuguojun
 */
public class UpdateByIdParser extends AbstractIdParser {
	
	public UpdateByIdParser(Serializable id){
		this.id = id;
	}

	public Object parse(Serializable s) throws Exception {
		List<Object> params = new ArrayList<Object>();
		StringBuilder sets = new StringBuilder();

		TableInfo tableInfo = getTableInfo(s);
		List<ColumnInfo> columnInfos = tableInfo.getColumnInfos(); // 字段信息
		for (int i = 0; i < columnInfos.size(); i++) {
			ColumnInfo columnInfo = columnInfos.get(i);
			// 如果是主键不提供更新功能
			if (columnInfo.isPrimaryKey()) {
				continue;
			}
			// 如果属性的值是null不提供更新功能
			String field = columnInfo.getField();
			Object value = BeanUtil.getFieldValue(s, field);
			if (value == null) {
				continue;
			}
			sets.append(columnInfo.getColumn()).append("=?, ");
			params.add(value);
		}

		StringBuilder sql = new StringBuilder();
		sql.append("update ").append(tableInfo.getTable()).append(" set ");
		sql.append(sets.substring(0, sets.length() - 2)).append(" where ");
		sql.append(getCondition(tableInfo.getPrimaryKeys(), params));

		return new Object[] { sql.toString(), params.toArray() };
	}

}
