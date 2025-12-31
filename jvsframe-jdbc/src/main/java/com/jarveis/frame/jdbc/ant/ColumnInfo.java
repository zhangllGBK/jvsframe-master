package com.jarveis.frame.jdbc.ant;

/**
 * 列信息类
 * 
 * @author liuguojun
 */
public class ColumnInfo {

	private String field; // 属性
	private String column; // 名称
	private boolean primaryKey; // 是否是主键

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getColumn() {
		if (column == null || "".equals(column)) {
			column = field;
		}
		return column;
	}

	public void setColumn(String column) {
		if (column == null || "".equals(column)) {
			this.column = field;
		} else {
			this.column = column;
		}
	}
	
	public boolean isPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(boolean primaryKey) {
		this.primaryKey = primaryKey;
	}
}
