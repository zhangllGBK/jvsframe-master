package com.jarveis.frame.dbs.filter;

import com.jarveis.frame.dbs.DbsCache;
import com.jarveis.frame.dbs.DbsConst;
import com.jarveis.frame.dbs.ErrorCode;
import com.jarveis.frame.dbs.ant.Interceptor;
import com.jarveis.frame.util.Param;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 流量限制
 *
 * @author liuguojun
 * @since  2019-12-03
 */
@Interceptor(code = "limit")
public class LimitFilter implements Filter {

	private static final Logger log = LoggerFactory.getLogger(LimitFilter.class);

	/**
	 * 所有请求的集合
	 */
	private final static Set<String> requestSet = new HashSet<>();
	/**
	 * 所有IP的集合
	 */
	private final static Map<String, AtomicInteger> ipMap = new HashMap<>();
	private int dbsLimitRequest;
	private int dbsLimitIpRequest;

	public int init() {
		// 总请求限制数
		dbsLimitRequest = NumberUtils.toInt(DbsCache.getConst(DbsConst.DBS_LIMIT_REQUEST), Integer.MAX_VALUE);
		// 每个ip的请求限制数
		dbsLimitIpRequest = NumberUtils.toInt(DbsCache.getConst(DbsConst.DBS_LIMIT_IP_REQUEST), Integer.MAX_VALUE);

		return 0;
	}

	public int filter(Param param) {
		try {
			// 获取日志忽略标识
			String tagName = param.getTagName();
			// 请求序列号
			String requestId = param.getHead().getString(Param.LABEL_REQUESTID);
			String remoteIp = param.getHead().getString(Param.LABEL_REMOTEIP);
			int errcode = param.getHead().getInteger(Param.LABEL_ERROR);

			if (Param.REQ.equals(tagName)) {

				if (requestSet.size() > dbsLimitRequest) {
					// 总请求数超限，则返回
					return ErrorCode.LIMIT_REQUEST;
				}

				if (ipMap.get(remoteIp) == null) {
					// 设置ip
					ipMap.put(remoteIp, new AtomicInteger(1));
				} else if (ipMap.get(remoteIp).get() > dbsLimitIpRequest) {
					// 如果ip请求超限，则返回
					return ErrorCode.LIMIT_REQUEST_IP;
				} else {
					// 请求开始时，ip计数递增
					ipMap.get(remoteIp).incrementAndGet();
				}

				// 将requestId添加到待处理集合
				requestSet.add(requestId);

			} else if (Param.RESP.equals(tagName) && errcode != ErrorCode.LIMIT_REQUEST && errcode != ErrorCode.LIMIT_REQUEST_IP) {

				// 请求完成后，ip计数递减
				int ipNum = ipMap.get(remoteIp).decrementAndGet();
				if (ipNum == 0) {
					ipMap.remove(remoteIp);
				}

				// 请求处理完成后，从处理集合中删除requestId
				requestSet.remove(requestId);

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
