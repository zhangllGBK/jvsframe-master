package com.jarveis.frame.sluice.core.bean;

import java.io.Serializable;

/**
 * Mapping Bean
 *
 * @author liuguojun
 * @since  2018-07-24
 */
public class Mapping implements Serializable {

	private String name;
	private String value;

	public Mapping() {
		super();
	}

	public String toString() {
		return "Mapping [name=" + name + ", value=" + value + "]";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
