package com.jarveis.frame.cache;

/**
 * 缓存异常类
 *
 * @author liuguojun
 * @since 2018-08-31
 */
public class CacheException extends RuntimeException {

	public CacheException(String s) {
		super(s);
	}

	public CacheException(String s, Throwable e) {
		super(s, e);
	}

	public CacheException(Throwable e) {
		super(e);
	}

}
