package com.jarveis.frame.dbs.bean;

/**
 * 过滤类
 * @author liuguojun
 * @since  2014-12-22
 */
public class FilterBean {

	private String id;
	private String clazz;
	private String type;
	private String match;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMatch() {
		return match;
	}

	public void setMatch(String match) {
		this.match = match;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof FilterBean)) {
			return false;
		}

		return getId().equals(((FilterBean) obj).getId());
	}
}
