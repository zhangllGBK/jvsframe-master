package com.jarveis.frame.dbs.filter;

import com.jarveis.frame.dbs.DbsCache;
import com.jarveis.frame.dbs.DbsConst;
import com.jarveis.frame.dbs.ErrorCode;
import com.jarveis.frame.dbs.ant.Interceptor;
import com.jarveis.frame.util.CharacterUtil;
import com.jarveis.frame.util.Param;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * ip过滤器
 *
 * @author liuguojun
 * @since  2022-07-21
 */
@Interceptor(code = "ip")
public class IpFilter implements Filter {

	private static final Logger log = LoggerFactory.getLogger(IpFilter.class);

	private static Set<String> allowIps = new HashSet<>();
	private static Set<String> denyIps = new HashSet<>();

	public int init() {
		// 允许的ip
		String allowIp = DbsCache.getConst(DbsConst.DBS_IP_ALLOW);
		if (StringUtils.isNotEmpty(allowIp)) {
			String[] arr = StringUtils.split(allowIp, CharacterUtil.SEPARATOR);
			for (String str : arr) {
				if (StringUtils.isNotEmpty(str)) {
					allowIps.add(str);
				}
			}
		}
		// 拒绝的ip
		String denyIp = DbsCache.getConst(DbsConst.DBS_IP_DENY);
		if (StringUtils.isNotEmpty(denyIp)) {
			String[] arr = StringUtils.split(denyIp, CharacterUtil.SEPARATOR);
			for (String str : arr) {
				if (StringUtils.isNotEmpty(str)) {
					denyIps.add(str);
				}
			}
		}
		return 0;
	}

	public int filter(Param param) {

		try {
			// 数据包类型
			String tagName = param.getTagName();
			// 请求ip
			String remoteIp = param.getHead().getString(Param.LABEL_REMOTEIP);

			if (Param.REQ.equals(tagName) && StringUtils.isNotEmpty(remoteIp)) {
				if (!allowIps.isEmpty() && !allowIps.contains(remoteIp)) {
					return ErrorCode.IP_REFUSE;
				}
				if (!denyIps.isEmpty() && denyIps.contains(remoteIp)) {
					return ErrorCode.IP_REFUSE;
				}
			}
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
		}

		return 0;
	}

	public int destory() {

		return 0;
	}

	/**
	 * 添加允许的ip
	 * @since 2024-08-05
	 * @param ip
	 */
	public static boolean addAllowIp(String ip) {
		return allowIps.add(ip);
	}

	/**
	 * 移除允许的ip
	 * @since 2024-08-05
	 * @param ip
	 */
	public static boolean removeAllowIp(String ip) {
		return allowIps.remove(ip);
	}

	/**
	 * 添加拒绝的ip
	 * @since 2024-08-05
	 * @param ip
	 */
	public static boolean addDenyIp(String ip) {
		return denyIps.add(ip);
	}

	/**
	 * 移除拒绝的ip
	 * @since 2024-08-05
	 * @param ip
	 */
	public static boolean removeDenyIp(String ip) {
		return denyIps.remove(ip);
	}
}
