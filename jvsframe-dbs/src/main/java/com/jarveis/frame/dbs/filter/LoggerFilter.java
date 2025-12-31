package com.jarveis.frame.dbs.filter;

import com.jarveis.frame.dbs.ant.Interceptor;
import com.jarveis.frame.util.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 系统日志过滤器
 * @author liuguojun
 * @since  2015-07-27
 */
@Interceptor(code = "logger")
public class LoggerFilter implements Filter {

	private static final Logger log = LoggerFactory.getLogger(LoggerFilter.class);

	public int init() {

		return 0;
	}

	public int filter(Param param) {

		try {
			if (log.isDebugEnabled()) {
				// 获取日志忽略标识, debug模式下，不压缩图片请求数据
				log.info(param.toXmlString());
			} else {
				// 获取日志忽略标识, 对请求的图片数据进行压缩
				log.info(param.toXmlString().replaceAll("data:image[\\/\\w\\;\\,\\+]+", "data:image...."));
			}
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
		}

		return 0;
	}

	public int destory() {

		return 0;
	}
}
