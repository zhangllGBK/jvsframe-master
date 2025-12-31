package com.jarveis.frame.dbs;

/**
 * 错误编号类
 * @author liuguojun
 * @since  2018-06-20 　
 */
public interface ErrorCode {

	/**
	 * 参数有误
	 */
	int PARAM_IN_NULL = 1999;

	/**
	 * IP被被拒绝
	 */
	int IP_REFUSE = 1998;

	/**
	 * 参数格式有误
	 */
	int PARAM_FORMAT = 1997;

	/**
	 * 服务编号有误
	 */
	int FUNCID_FORTMAT = 1996;

	/**
	 * 服务不存在
	 */
	int SERVICE_NOTEXIST = 1995;

	/**
	 * 私有服务不可访问
	 */
	int SERVICE_ACCESS_REFUSED = 1994;

	/**
	 * 用户请求数超限，服务器繁忙
	 */
	int LIMIT_REQUEST = 1993;

	/**
	 * 状态码有误（1000~9999）
	 */
	int ERRCODE_FORMAT = 1992;

	/**
	 * IP请求数超限，服务器繁忙
	 */
	int LIMIT_REQUEST_IP = 1991;
	
	/**
	 * 远程主机超时异常
	 */
	int REMOTE_RENULL= 1990;


}
