package com.jarveis.frame.jdbc;

/**
 * 数据源包装类
 * 
 * @author liuguojun
 */
public class DataSourceWrapper {

	private String id; // 标识
	private boolean df = false; // 默认
	private Object dataSource; // 数据源

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean getDf() {
		return df;
	}

	public void setDf(boolean df) {
		this.df = df;
	}

	public Object getDataSource() {
		return dataSource;
	}

	public void setDataSource(Object dataSource) {
		this.dataSource = dataSource;
	}
}
