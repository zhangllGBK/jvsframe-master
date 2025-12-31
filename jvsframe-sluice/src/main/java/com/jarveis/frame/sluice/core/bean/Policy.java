package com.jarveis.frame.sluice.core.bean;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * Policy Bean
 * 服务对应的拦截规则（一条或多条）
 *
 * @author liuguojun
 * @since  2018-07-24
 */
public class Policy implements Serializable {

	private String funcId;
	private List<String> rules;

	public Policy() {
		super();
	}

	@Override
	public String toString() {
		return "Policy [funcId=" + funcId + "]";
	}

	public String getFuncId() {
		return funcId;
	}

	public void setFuncId(String funcId) {
		this.funcId = funcId;
	}

	public List<String> getRules() {
		return rules;
	}

	public void setRules(List<String> rules) {
		this.rules = rules;
		Collections.sort(this.rules);
	}

	public void addRules(List<String> rules) {
		boolean issort = false;
		for (String rule : rules) {
			if (!this.rules.contains(rule)) {
				this.rules.add(rule);
				issort = true;
			}
		}
		if (issort) {
			Collections.sort(this.rules);
		}
	}

}
