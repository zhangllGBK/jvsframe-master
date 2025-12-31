package com.jarveis.frame.dbs;

import com.jarveis.frame.dbs.filter.Filter;

/**
 * 服务包装类
 * 
 * @author liuguojun
 * @since 2020-09-18
 */
public class FilterWrapper {

	/**
	 * 拦截器编号
	 */
	private final String code;

	/**
	 * 拦截器
	 */
	private final Filter filter;


	/**
	 * 构造方法
	 *
	 * @param code 拦截器编号
	 * @param filter 拦截器
	 */
	public FilterWrapper(String code, Filter filter) {
		this.code = code;
		this.filter = filter;
	}

	/**
	 * 获取服务编号
	 *
	 * @return 返回拦截器编号
	 */
	public String getCode() {
		return code;
	}



	/**
	 * 获取拦截器
	 * 
	 * @return 拦截器实例
	 */
	public Filter get() {
		return filter;
	}

}
