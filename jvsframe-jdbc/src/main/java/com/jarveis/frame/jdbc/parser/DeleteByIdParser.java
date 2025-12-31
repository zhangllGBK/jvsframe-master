package com.jarveis.frame.jdbc.parser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.jarveis.frame.jdbc.ant.TableInfo;

/**
 * 对象的删除语句解析类
 * 
 * @author liuguojun
 */
public class DeleteByIdParser extends AbstractIdParser {

	public DeleteByIdParser(Serializable id) {
		this.id = id;
	}

	public Object parse(Serializable s) throws Exception {
		List<Object> params = new ArrayList<Object>();
		
		TableInfo tableInfo = getTableInfo(s);
		StringBuffer sql = new StringBuffer("delete from ");
		sql.append(tableInfo.getTable()).append(" where ");
		sql.append(getCondition(tableInfo.getPrimaryKeys(), params));

		return new Object[] { sql.toString(), params.toArray() };
	}
}
