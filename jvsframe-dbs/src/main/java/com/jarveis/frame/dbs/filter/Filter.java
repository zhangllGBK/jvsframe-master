package com.jarveis.frame.dbs.filter;

import com.jarveis.frame.util.Param;

/**
 * 过滤器接口
 * @author liuguojun
 * @since 2014-12-22
 */
public interface Filter {

	String LABEL_SLUICE_ERROR_DESC = "@_filter_errdesc";
	
	public int init();

	public int filter(Param in);

	public int destory();
}
