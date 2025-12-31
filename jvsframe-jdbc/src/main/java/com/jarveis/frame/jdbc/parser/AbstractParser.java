package com.jarveis.frame.jdbc.parser;

import java.io.Serializable;

import com.jarveis.frame.bean.BeanUtil;
import com.jarveis.frame.jdbc.JdbcCache;
import com.jarveis.frame.jdbc.SqlParser;
import com.jarveis.frame.jdbc.ant.TableInfo;

/**
 * 抽象的SQL解析器
 * 
 * @author liuguojun
 */
public abstract class AbstractParser implements SqlParser {

	/**
	 * 获取表信息
	 * 
	 * @param s
	 * @return TableInfo
	 */
	protected TableInfo getTableInfo(Serializable s) {
		String name = BeanUtil.getFname(s);
		TableInfo tableInfo = JdbcCache.getTable(name);
		if (tableInfo == null) {
			tableInfo = new TableInfo(s);
			JdbcCache.putTable(name, tableInfo);
		}
		
		return tableInfo;
	}
}
