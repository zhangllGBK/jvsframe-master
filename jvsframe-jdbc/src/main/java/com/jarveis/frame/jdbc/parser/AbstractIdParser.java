package com.jarveis.frame.jdbc.parser;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import com.jarveis.frame.bean.BeanUtil;
import com.jarveis.frame.jdbc.ant.ColumnInfo;
import com.jarveis.frame.jdbc.ant.CompositeKey;

/**
 * 
 * @author liuguojun
 */
public abstract class AbstractIdParser extends AbstractParser {

	protected Serializable id;

	/**
	 * 获取由主键构建的条件
	 * 
	 * @param primaryKeys
	 * @param params
	 * @return
	 */
	protected String getCondition(List<ColumnInfo> primaryKeys,
			List<Object> params) {
		StringBuffer condition = new StringBuffer();

		if (id instanceof CompositeKey) {
			for (Iterator<ColumnInfo> it = primaryKeys.iterator(); it.hasNext();) {
				ColumnInfo columnInfo = it.next();
				String field = columnInfo.getField();
				Object value = BeanUtil.getFieldValue(id, field);
				if (value == null) {
					new SQLException(
							"delete result of table by compositekey，but compositekey value is empty or null");
				}
				condition.append(columnInfo.getColumn()).append("=? and ");
				params.add(value);
			}
			int length = condition.length();
			condition = condition.delete(length - 5, length);
		} else {
			ColumnInfo columnInfo = primaryKeys.get(0);
			if (id == null) {
				new SQLException(
						"delete result of table by primarykey，but primarykey value is empty or null");
			}
			condition.append(columnInfo.getColumn()).append("=?");
			params.add(id);
		}

		return condition.toString();
	}
}
