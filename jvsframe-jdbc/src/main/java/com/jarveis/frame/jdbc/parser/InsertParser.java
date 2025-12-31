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
public class InsertParser extends AbstractParser {

	public Object parse(Serializable s) throws Exception {
		List<Object> params = new ArrayList<Object>();
		StringBuffer names = new StringBuffer();
		StringBuffer values = new StringBuffer();
		
		TableInfo tableInfo = getTableInfo(s);
		List<ColumnInfo> columnInfos = tableInfo.getColumnInfos(); // 字段信息
		for (int i = 0; i < columnInfos.size(); i++) {
			ColumnInfo columnInfo = columnInfos.get(i);
			String filed = columnInfo.getField();
			String column = columnInfo.getColumn();
			Object value = BeanUtil.getFieldValue(s, filed);
			if (value == null) {
				continue;
			}
			names.append(column).append(", ");
			values.append("?, ");
			params.add(value);
		}
		StringBuffer sql = new StringBuffer();
		sql.append("insert into ").append(tableInfo.getTable()).append("(");
		sql.append(names.substring(0, names.length() - 2)).append(") values(");
		sql.append(values.substring(0, values.length() - 2)).append(")");

		return new Object[] { sql.toString(), params.toArray() };
	}

}
