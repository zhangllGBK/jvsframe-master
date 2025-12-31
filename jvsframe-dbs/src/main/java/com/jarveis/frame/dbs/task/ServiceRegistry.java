package com.jarveis.frame.dbs.task;

import com.jarveis.frame.dbs.*;
import com.jarveis.frame.dbs.ant.Scope;
import com.jarveis.frame.util.CharacterUtil;
import com.jarveis.frame.util.HttpUtil;
import com.jarveis.frame.util.Param;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DBS服务发布与订阅任务
 * <pre>
 *     当前任务，适用于dbs服务集群，单体应用并不需要；
 *     dbs_local="http://127.0.0.1:xxxx"
 *     dbs_node="serviceNode"
 *     dbs_name_server="http://127.0.0.1:xxxx"
 *     dbs_subscribe="*"
 *
 *     DBS服务发布：
 *     考虑到Dbs服务需要集中管理，默认本地服务都需要进行集中进行管理，所以本地服务需要发布到Dbs Server
 *
 *     DBS服务订阅
 *     各Dbs Node先将服务提交到Dbs Server，本地节点通过服务器订阅其它节点的服务
 * </pre>
 *
 * @author liuguojun
 * @since 2019-11-18
 * @see com.jarveis.frame.dbs.DbsConst
 * @see com.jarveis.frame.dbs.service.RegistryService
 */
public class ServiceRegistry {

    private static final Logger log = LoggerFactory.getLogger(ServiceRegistry.class);

    // 是否开启发布模式(默认开启)
    private boolean isPublish = true;
    // 发布的服务（正则表达式）
    private String publish = "\\d{5}";

    // 是否开启订阅模式
    private boolean isSubscribe = false;
    // 订阅的服务（正则表达式）
    private String subscribe = StringUtils.EMPTY;

    // 本地访问远程服务是否需要代理
    private boolean isProxy = false;
    // serviceServer节点
    private String[] servers;
    // 本地节点URL
    private String localNode;

    /**
     * 初始化
     */
    public void init(){
        // 服务器节点
        String serviceServer = DbsCache.getConst(DbsConst.DBS_SERVICE_SERVER);
        if (StringUtils.isEmpty(serviceServer)) {
            return;
        }

        // 注册服务器
        servers = StringUtils.split(serviceServer, CharacterUtil.SEPARATOR);

        // 本地节点
        localNode = DbsCache.getConst(DbsConst.DBS_LOCAL);
        if (StringUtils.isEmpty(localNode)) {
            return;
        }

        // 发布　
        // TODO 考虑到服务要进行管理，默认将本地服务都发布的服务器
        // 但同时要面临需求问题，就是按需发发布，有些不服务不允许被其它服务器所使用
        // 可以要将serviceServer进行控制，如果远程服务器要订阅的服务，包含了其它服务器上不能发布的服务，则不会返回给订阅服务器
        /*publish = DbsCache.getConst(DbsConst.DBS_PUBLISH);
        if (StringUtils.isNotEmpty(publish)) {
            isPublish = true;
        }*/

        // 订阅
        subscribe = DbsCache.getConst(DbsConst.DBS_SUBSCRIBE);
        if (StringUtils.isNotEmpty(subscribe)) {
            isSubscribe = true;
        }

        // 服务发布和订阅模式
        String servicePattern = DbsCache.getConst(DbsConst.DBS_SERVICE_PATTERN);
        if (StringUtils.isEmpty(servicePattern)) {
            servicePattern = StringUtils.EMPTY;
        }
        String[] patterns = StringUtils.split(servicePattern, CharacterUtil.SEPARATOR);
        for (String pattern : patterns) {
            if ("publish".equals(pattern)) {
                isPublish = true;
                if (StringUtils.isEmpty(publish)) {
                    publish = "\\d{5}";
                }
            } else if ("subscribe".equals(pattern)) {
                isSubscribe = true;
                if (StringUtils.isEmpty(subscribe)) {
                    subscribe = "\\d{5}";
                }
            }
        }

        // 获取远程服务访问是否支持代理
        String isProxyStr = DbsCache.getConst(DbsConst.DBS_ISPROXY);
        if (StringUtils.isNotEmpty(isProxyStr)) {
            isProxy = BooleanUtils.toBoolean(isProxyStr);
        }
    }

    /**
     *
     */
    public void execute() {
        if (!isPublish && !isSubscribe) {
            return;
        }

        // 同步时请求参数对象
        Param in, out;
        // 同步时返回消息
        String message;
        Map<String, String> params = new HashMap<>(1);
        try {
            in = new Param(Param.REQ);
            in.getHead().setProperty(Param.LABEL_FUNCID, ServiceCode.DBS_REGISTRY_SERVICE);
            in.getHead().setProperty(Param.LABEL_DATATYPE, Param.DT_JSON);
            // 告知serviceServer，本地订阅的服务，由于serviceServer的服务过多，不需要将所有服务都给本地服务器，按需订阅。
            in.getBody().setProperty("@subscribe", subscribe);

            if (isPublish) {
                // 注册本地节点的服务（不包含已同步的其它节点服务）
                publish(localNode, in);
            }

            // 遍历当前集群列表
            for (String server : servers) {
                params.put("_message", in.toXmlString());
                String remoteNode = DbsUtils.getDbsURI(server);
                message = HttpUtil.doPost(remoteNode, params);
                if (StringUtils.isNotEmpty(message)) {
                    if (log.isDebugEnabled()) {
                        log.debug("注册本地服务(" + localNode + ")到远程服务器(" + remoteNode + ")");
                    }
                    out = new Param(message);
                    if (Param.ERROR_SUCCESS.equals(out.getHead().getString(Param.LABEL_ERROR))) {
                        // 添加服务
                        if (isSubscribe) {
                            subscribeService(localNode, remoteNode, out.getBody().getChilds("service"));
                            subscribeConst(localNode, remoteNode, out.getBody().getChilds("constant"));
                        }
                    }
                } else {
                    log.warn("同步失败（" + localNode + " -x-> " + remoteNode + "）");
                }
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    /**
     * 发布本地服务到serviceServer
     *
     * @param currentNode 当前节点
     * @param in          输入参数
     */
    private void publish(String currentNode, Param in) {
        // 当前节点的所有服务（包含已同步的其它节点服务）
        List<String> allServiceKeys = DbsCache.listServiceKey();
        // 构建同步到master的服务
        for (String sk : allServiceKeys) {
            ServiceWrapper wrapper = DbsCache.getService(sk);
            if (wrapper == null) {
                // 异常服务
                continue;
            }
            if (StringUtils.isNumeric(sk) && Integer.parseInt(sk) < 10000) {
                // 如果是系统内服务，则不需要同步
                continue;
            }
            if (wrapper.getScope() == Scope.PRIVATE) {
                // 如果服务的作用域是private,　则不可被外部访问
                continue;
            }
            if (!wrapper.isLocalService()) {
                continue;
            }
            Param serviceParam = in.getBody().addParam("service");
            serviceParam.setProperty("@node", currentNode);
            serviceParam.setProperty("@func", sk);
            serviceParam.setProperty("@before", StringUtils.join(wrapper.getBefores(),","));
            serviceParam.setProperty("@after", StringUtils.join(wrapper.getAfters(),","));
        }
    }

    /**
     * 从服务器订阅服务信息
     *
     * @param currentNode   当前节点
     * @param serverNode    远程服务器节点
     * @param servicesParam 服务参数
     */
    private void subscribeService(String currentNode, String serverNode, List<Param> servicesParam) {
        if (servicesParam == null) {
            return;
        }
        HashMap<String, ServiceWrapper> serviceMap = new HashMap<>();
        for (Param param : servicesParam) {
            String node = param.getString("@node"); // 服务节点
            String funcId = param.getString("@func"); // 服务编号
            // String proxy = param.getString("@proxy"); // 代理节点
            // String before = param.getProperty("@before"); // 前置拦截
            // String after = param.getProperty("@after"); // 后置拦截

            // 参数校验
            if (StringUtils.isEmpty(funcId) || StringUtils.isEmpty(node) || node.equals(currentNode)) {
                continue;
            }

            ServiceWrapper wrapper = DbsCache.getService(funcId);
            boolean issyn = (wrapper == null) ? true : (wrapper.isLocalService()) ? false : true;
            // 如果
            if (issyn) {
                if (wrapper == null) {
                    // 如果不存在，则新建
                    wrapper = new ServiceWrapper(funcId);
                    // 修改注册的服务为私有服务，防止内部服务暴露
                    // wrapper.setScope(Scope.PRIVATE);
                    DbsCache.putRemoteService(funcId, wrapper);
                }

                boolean result;
                if (isProxy) {
                    result = wrapper.add(serverNode);
                    if (result) {
                        if (log.isDebugEnabled()) {
                            log.debug("add service, from proxy=" + serverNode + " of service=" + funcId);
                        }
                    }
                } else {
                    result = wrapper.add(node);
                    if (result) {
                        if (log.isDebugEnabled()) {
                            log.debug("add service, from node=" + node + " of service=" + funcId);
                        }
                    }
                }

            }

        }

        DbsCache.putRemoteService(serviceMap);
    }


    /**
     * 从服务器订阅常量信息
     *
     * @param currentNode  当前节点
     * @param serverNode   远程服务器节点
     * @param constParams  常量节点集合
     */
    private void subscribeConst(String currentNode, String serverNode, List<Param> constParams) {
        if (constParams == null) {
            return;
        }
        for (Param param : constParams) {
            String name = param.getString("@name"); // 键
            String value = param.getString("@value"); // 值
   
            DbsCache.putConst(name, value);
        }
    }
}
