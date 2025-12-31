package com.jarveis.frame.dbs.bean;

import com.jarveis.frame.dbs.ant.Scope;


/**
 * 功能类
 * @author liuguojun
 * @since  2014-06-05
 */
public class FunctionBean {

	private String code;
	private String clazz;
	private String init;
	private String desc;
	private Boolean transaction;
	private Scope scope;
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	public String getInit() {
		return init;
	}

	public void setInit(String init) {
		this.init = init;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public Boolean getTransaction() {
		return transaction;
	}

	public void setTransaction(Boolean transaction) {
		this.transaction = transaction;
	}

	public Scope getScope() {
		return scope;
	}

	public void setScope(Scope scope) {
		this.scope = scope;
	}
}
