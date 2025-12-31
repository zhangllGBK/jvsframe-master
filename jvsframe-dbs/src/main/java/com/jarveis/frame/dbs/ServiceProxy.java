package com.jarveis.frame.dbs;

import com.jarveis.frame.dbs.ant.Scope;
import com.jarveis.frame.util.HttpUtil;
import com.jarveis.frame.util.Param;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 服务代理
 *
 * @author liuguojun
 * @since 2018-03-12
 */
public class ServiceProxy {

    private static final Logger log = LoggerFactory.getLogger(ServiceProxy.class);

    public static final String SCOPE = "scope";

    /**
     * 代理执行服务
     *
     * @param funcId 服务编号
     * @param in     请求参数
     * @return 输出数据包
     * @throws Exception 异常
     */
    public static Param callService(String funcId, Param in) throws Exception {
        Param out = new Param(Param.RESP);

        ServiceWrapper serviceWrapper = DbsCache.getService(funcId);
        if (serviceWrapper != null) {

            Scope scope = serviceWrapper.getScope();
            String extend = in.getHead().getString(Param.LABEL_EXTEND);
            in.getHead().removeProperty(Param.LABEL_EXTEND);

            // 验证服务的作用域，是否有访问权限
            if (SCOPE.equals(extend) && scope == Scope.PRIVATE) {
                out.getHead().setProperty(Param.LABEL_ERROR, ErrorCode.SERVICE_ACCESS_REFUSED);
                return out;
            }

            if (serviceWrapper.isLocalService()) {
                // 考虑到本地服务相互调用并不会走LoggerFilter，这样就没有输入日志
                if (StringUtils.isEmpty(extend)) {
                    log.info(in.toXmlString());
                }
                // 调用本地服务
                out = callLocalService((Service) serviceWrapper.get(), in);
                // 考虑到本地服务相互调用并不会走LoggerFilter，这样就没有输出日志
                if (StringUtils.isEmpty(extend)) {
                    log.info(out.toXmlString());
                }
            } else {
                // 调用远程服务
                in.getHead().setProperty(Param.LABEL_FUNCID, funcId);
                // 配置的超时时间
                String timeoutStr = DbsCache.getConst(DbsConst.DBS_REMOTE_SERVICE_TIMEOUT);
                // 配置的重试次数
                String retryStr = DbsCache.getConst(DbsConst.DBS_REMOTE_SERVICE_RETRY);
                int retryInt = NumberUtils.toInt(retryStr, 1);
                int errcode;
                for (int i = 0; i < retryInt; i++) {
                    String service = (String) serviceWrapper.get();
                    if (service == null) {
                        // 服务不存在
                        out.getHead().setProperty(Param.LABEL_FUNCID, funcId);
                        out.getHead().setProperty(Param.LABEL_ERROR, ErrorCode.SERVICE_NOTEXIST);
                    } else {
                        // 调用远程服务
                        String host = DbsUtils.getDbsURI(service);
                        out = callRemoteService(host, in, timeoutStr);
                    }

                    errcode = out.getHead().getInteger(Param.LABEL_ERROR);
                    if (ErrorCode.REMOTE_RENULL != errcode || ErrorCode.SERVICE_NOTEXIST != errcode) {
                        // 如果调用服务返回的不是主机超时或服务不存在，则退出重试
                        serviceWrapper.add(service);
                        break;
                    }
                }
            }

        } else {
            out.getHead().setProperty(Param.LABEL_ERROR, ErrorCode.SERVICE_NOTEXIST);
        }

        return out;
    }

    /**
     * 执行本地的服务
     *
     * @param service 服务对象
     * @param in      请求参数
     * @return 返回参数
     * @throws Exception
     */
    private static Param callLocalService(Service service, Param in) throws Exception {
        // 获取请求头信息
        Map<String, String> headProps = in.getHead().getPropertys();

        // 　调用本地服务
        Param out = service.callService(in);
        if (out == null) {
            out = new Param(Param.RESP);
            out.getHead().setProperty(Param.LABEL_ERROR, Param.ERROR_EXCEPTION);
        }

        // 重置head头信息
        Set entrys = headProps.entrySet();
        for (Object obj :entrys) {
            Map.Entry<String, String> entry = (Map.Entry<String, String>) obj;
            String value = out.getHead().getString("@" + entry.getKey());
            if (StringUtils.isEmpty(value)) {
                out.getHead().setProperty("@" + entry.getKey(), entry.getValue());
            }
        }

        return out;
    }

    /**
     * 执行远程的服务
     *
     * @param host       远程节点
     * @param in         请求参数
     * @param timeoutStr 超时设置
     * @return 返回参数
     * @throws Exception
     */
    private static Param callRemoteService(String host, Param in, String timeoutStr) throws Exception {
        String _message = null;

        // 获取请求头信息
        Map<String, String> headProps = in.getHead().getPropertys();

        // 内部调用不使用加密传输
        in.getHead().removeProperty(Param.LABEL_ENCRYPTMODE);
        in.getHead().removeProperty(Param.LABEL_ENCRYPTKEY);

        if (StringUtils.isNotEmpty(host)) {
            Map<String, String> params = new HashMap<String, String>(1);
            params.put("_message", in.toXmlString());
            // 设置超时时间
            int timeout = NumberUtils.toInt(timeoutStr, HttpUtil.DEFAULT_TIMEOUT);
            // http请求
            if (log.isDebugEnabled()) {
                log.debug("remote request: " + host + "?_message=" + in.toXmlString());
            }
            _message = HttpUtil.doPost(host, params, timeout, timeout, timeout);
            if (log.isDebugEnabled()) {
                log.debug("remote response: " + _message);
            }
        }

        if (StringUtils.isEmpty(_message)) {
            log.warn("remote(" + host + ")连接有误");
            in.getBody().removePropertys();
            in.getHead().setProperty(Param.LABEL_ERROR, ErrorCode.REMOTE_RENULL);
            _message = in.toXmlString();
        }

        Param out = new Param(_message);
        out.getHead().setPropertys(headProps);

        return out;
    }

}