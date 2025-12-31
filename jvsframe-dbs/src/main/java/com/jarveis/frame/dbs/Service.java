package com.jarveis.frame.dbs;

import com.jarveis.frame.util.Param;

/**
 * 服务接口（非线程安全，其接口实现类，禁止使用共享变量）
 * 
 * @author liuguojun
 * @since  2014-06-12
 */
public interface Service {

	/**
	 * 功能入口
	 * 
	 * @param in 请求数据
	 * @return 返回数据
	 */
	public Param callService(Param in);

}
