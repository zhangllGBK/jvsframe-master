package com.jarveis.frame.dbs.filter;

import com.jarveis.frame.dbs.*;
import com.jarveis.frame.dbs.ant.Interceptor;
import com.jarveis.frame.util.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;

/**
 * 参数对象校验
 * @author liuguojun
 * @since  2016-01-10
 */
@Interceptor(code = "param")
public class ParamFilter implements Filter {

	private static final Logger log = LoggerFactory.getLogger(ParamFilter.class);

	public int init() {

		return 0;
	}

	public int filter(Param param) {

		if (param == null) {
			return ErrorCode.PARAM_IN_NULL;
		}

		try {
			String tagName = param.getTagName();
			if (Param.REQ.equals(tagName)) {
				String funcId = param.getHead().getString(Param.LABEL_FUNCID);

				// 拦截表头的xss攻击
				Map<String, String> headProps = param.getHead().getPropertys();
				Set<Map.Entry<String, String>> propSets = headProps.entrySet();
				for (Map.Entry<String, String> entry : propSets) {
					String key = entry.getKey();
					String value = entry.getValue();

					int sz = value.length();
					for (int i = 0; i < sz; i++) {
						char ch = value.charAt(i);
						// [ '*+,-.','0-9', 'A~Z', '_', 'a~z']
						boolean bl = (ch >= 42 && ch <= 46) || (ch >= 48 && ch <= 57) || (ch >= 65 && ch <= 90) || ch == 95 || (ch >= 97 && ch <= 122);
						if (!bl) {
							param.getHead().removePropertys();
							param.getBody().removePropertys();
							return ErrorCode.PARAM_FORMAT;
						}
					}
				}

				// 验证参数是否正确
				if (DbsConst.DBS_NODE_SERVICE.equals(DbsCache.getConst(DbsConst.DBS_NODE)) && DbsUtils.isFuncId(funcId) < 1) {
					param.getHead().removePropertys();
					param.getBody().removePropertys();
					return ErrorCode.FUNCID_FORTMAT;
				}

				// 验证功能是否存在
				ServiceWrapper service = DbsCache.getService(funcId);
				if (service == null) {
					param.getHead().removePropertys();
					param.getBody().removePropertys();
					return ErrorCode.SERVICE_NOTEXIST;
				}

			} else if (Param.RESP.equals(tagName)) {
				int errcode = param.getHead().getInteger(Param.LABEL_ERROR);
				if (errcode > 0 && errcode < 1000 || errcode > 9999) {
					return ErrorCode.ERRCODE_FORMAT;
				}
			} else {
				return ErrorCode.PARAM_FORMAT;
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
