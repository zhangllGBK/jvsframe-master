package com.jarveis.frame.dbs;

import com.jarveis.frame.dbs.filter.Filter;
import com.jarveis.frame.dbs.filter.FilterProxy;
import com.jarveis.frame.util.Param;
import com.jarveis.frame.util.ParamException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * desc 数据访问入口
 *
 * @author liuguojun
 * @since  2014-06-05
 */
public class DbsDispatcher {

    private static final Logger log = LoggerFactory.getLogger(DbsDispatcher.class);

    /**
     * 默认的前置拦截器
     */
    private static final String[] beforeFilters = {"logger", "param", "statistics", "ip", "limit"};

    /**
     * 默认的后置拦截器
     */
    private static final String[] afterFilters = {"statistics", "logger"};


    /**
     * 兼容2.x版本
     *
     * @param in 请求数据对象
     * @return String
     * @throws Exception 异常
     * @deprecated
     */
    public static String process(String in) throws Exception {
        return process(new Param(in)).toXmlString();
    }

    /**
     * 处理请求
     *
     * <pre>
     * in = {
     * 	head:{
     * 		appId: '客户端标识',
     * 		appVersion: '版本号',
     * 		token: '服务器编码',
     * 		device: '设备编码',
     * 		funcId: '功能编码',
     * 		dataType: '数据类型'
     *    }
     * 	body:{
     * 		name: 'Tom' // 功能所需参数
     *    }
     * }
     * </pre>
     *
     * @param in 请求数据对象
     * @throws Exception 异常
     */
    public static Param process(Param in) throws Exception {
        // 获取请求对象
        if (in == null) {
            return reResponse(ErrorCode.PARAM_IN_NULL);
        }

        Param out = null;

        // 前置拦截
        int errCode = filter(0, in);
        if (errCode > 0) {
            // 输入头信息
            Map<String, String> inHeads = in.getHead().getPropertys();
            String filterErrdesc = in.getHead().getString(Filter.LABEL_SLUICE_ERROR_DESC);
            if (StringUtils.isNotEmpty(filterErrdesc)) {
                inHeads.put(Param.LABEL_ERROR_DESC, filterErrdesc);
            }
            out = reResponse(errCode, inHeads);
        }

        // 扩展字段，开启作用域设置
        if (out == null) {
            // 获取服务编码
            String funcId = in.getHead().getString(Param.LABEL_FUNCID);
            in.getHead().setProperty(Param.LABEL_EXTEND, ServiceProxy.SCOPE);
            out = ServiceProxy.callService(funcId, in);
        }

        // 后置拦截
        errCode = filter(1, out);
        if (errCode > 0) {
            // 输入头信息
            Map<String, String> inHeads = in.getHead().getPropertys();
            String filterErrdesc = out.getHead().getString(Filter.LABEL_SLUICE_ERROR_DESC);
            if (StringUtils.isNotEmpty(filterErrdesc)) {
                inHeads.put(Param.LABEL_ERROR_DESC, filterErrdesc);
            }
            out = reResponse(errCode, inHeads);
        }

        return out;
    }

    /**
     * 获取请求对象
     *
     * @param message 请求消息
     * @return 响应数据对象
     */
    private static Param getReqParam(String message) {
        Param in = null;

        try {
            in = new Param(message);
        } catch (ParamException ex) {
            log.error(ex.getMessage(), ex);
        }

        return in;
    }

	/**
	 * 返回消息
	 * @param errCode 错误编码
	 * @return 响应数据对象
	 */
	private static Param reResponse(int errCode) {
        Map<String, String> heads = new HashMap<>();
        heads.put(Param.LABEL_DATATYPE.substring(1), Param.DT_XML);

        return reResponse(errCode, heads);
    }

    /**
     * 返回消息
     *
     * @param errCode 错误编码
     * @param heads 头信息集合
     * @return 响应数据对象
     */
    private static Param reResponse(int errCode, Map<String, String> heads) {
        try {
            Param out = new Param("Resp");
            out.getHead().setProperty(Param.LABEL_ERROR, errCode);

            return reResponse(out, heads);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }

        return null;
    }

    /**
     * 返回消息
     *
     * @param out 输出对象
     */
    private static Param reResponse(Param out, Map<String, String> heads) {
        out.getHead().setPropertys(heads);

        return out;
    }

    /**
     * 过滤器
     *
     * @param filterType 过滤器类型
     * @param param      参数对象
     * @return 错误状态码
     */
    private static int filter(int filterType, Param param) {
        int errCode = NumberUtils.INTEGER_ZERO;
        int reCode = NumberUtils.INTEGER_ZERO;
        try {
            // 功能编码
            String funcId = param.getHead().getString(Param.LABEL_FUNCID);
            ServiceWrapper sw = DbsCache.getService(funcId);

            if (filterType%2 == 0) {
                // 先执行默认的拦截器
                errCode = filter(Arrays.asList(beforeFilters), param);
                // 再执行自定义的拦截器
                if (sw != null) {
                    reCode = filter(sw.getBefores(), param);
                }
            } else {
                // 先执行自定义的拦截器
                if (sw != null) {
                    reCode = filter(sw.getAfters(), param);
                }
                // 再执行默认的拦截器
                errCode = filter(Arrays.asList(afterFilters), param);
            }

            if (errCode == 0 && reCode > 0) {
                errCode = reCode;
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }

        return errCode;
    }

    /**
     * 系统默认的拦截器
     *
     * @param filters 拦截器集
     * @param param 数据对象
     * @return 错误编码
     */
    private static int filter(List<String> filters, Param param) {
        int errCode = NumberUtils.INTEGER_ZERO;

        try {
            for (String s : filters) {
                // 获取系统拦截器
                FilterWrapper fw = DbsCache.getFilter(s);
                if (fw == null || fw.get() == null) {
                    continue;
                }

                // 拦截器返回的编码
                int reCode = FilterProxy.callFilter(fw.get(), param);
                if (errCode == 0 && reCode > 0) {
                    // 错误编码只进行一次有效赋值
                    errCode = reCode;
                }
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }

        return errCode;
    }

}
