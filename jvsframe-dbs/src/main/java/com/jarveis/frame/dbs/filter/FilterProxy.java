package com.jarveis.frame.dbs.filter;

import com.jarveis.frame.util.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 过滤器代理
 * @author liuguojun
 * @since  2015-08-09
 */
public class FilterProxy {

	private static final Logger log = LoggerFactory.getLogger(FilterProxy.class);

	/**
	 * 代理执行
	 * 
	 * @param filter 过滤器
	 * @param vo 输入/输出数据包
	 * @return ParamVO
	 */
	public static int callFilter(Filter filter, Param vo) {
		int errcode = 0;
		
		try {
			errcode = filter.filter(vo);
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
		}

		return errcode;
	}
}
