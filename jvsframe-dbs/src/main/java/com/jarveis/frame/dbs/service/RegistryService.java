package com.jarveis.frame.dbs.service;

import com.jarveis.frame.dbs.*;
import com.jarveis.frame.dbs.ant.Function;
import com.jarveis.frame.dbs.ant.Scope;
import com.jarveis.frame.util.CharacterUtil;
import com.jarveis.frame.util.Param;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 注册服务（集群-主备）
 * <pre>
 *     1.支持服务注册，订阅
 *     2.支持配置订阅
 * </pre>
 * @see com.jarveis.frame.dbs.task.ServiceRegistry
 *
 * @author liuguojun
 */
@Function(code = ServiceCode.DBS_REGISTRY_SERVICE)
public class RegistryService implements Service {

	private static final Logger log = LoggerFactory.getLogger(RegistryService.class);

	/**
	 * 注册服务的节点地址
	 */
	private static final ThreadLocal<String> registryNodeThreadLocal = new ThreadLocal<>();

	public Param callService(Param in) {
		Param out = null;

		try {
			if (log.isDebugEnabled()) {
				log.debug(in.toXmlString());
			}
			out = new Param(Param.RESP);

			// 订单阅服务
			String subscribe = in.getBody().getString("@subscribe"); // 请求参数

			support2x(in);

			// 封装请求参数
			List<Param> serviceParams = in.getBody().getChilds("service"); // 请求参数
			// 注册服务3.x版本
			for (Param serviceParam : serviceParams) {
				registryService(serviceParam);
			}

			out.getHead().setProperty(Param.LABEL_ERROR, Param.ERROR_SUCCESS);

			// 发布服务
			publishService(out, subscribe);

			if (log.isDebugEnabled()) {
				log.debug(out.toXmlString());
			}

		} catch (Exception ex) {
			if (out != null) {
				out.getHead().setProperty(Param.LABEL_ERROR, Param.ERROR_EXCEPTION);
			}
			log.error(ex.getMessage(), ex);
		}

		return out;
	}

	/**
	 * 用于支持2.x版本的注册
	 *
	 * @param in 输入对象
	 */
	private void support2x(Param in) {
		// 封装请求参数
		String remoteNode = in.getBody().getString("@node"); // 请求参数
		if (StringUtils.isNotEmpty(remoteNode)) {
			remoteNode = DbsUtils.getDbsURI(remoteNode);
		}
		String services = in.getBody().getString("@services"); // 请求参数
		if (StringUtils.isNotEmpty(services)) {
			String[] serviceArr = StringUtils.split(services, CharacterUtil.SEPARATOR);
			for (String service : serviceArr) {
				Param serviceParam = in.getBody().addParam("service");
				serviceParam.setProperty("@node", remoteNode);
				serviceParam.setProperty("@func", service);
			}
		}
	}

	/**
	 * 注册service
	 * 
	 * @param serviceParam service配置对象
	 */
	private void registryService(Param serviceParam) {
		if (serviceParam == null) {
			return ;
		}

		String node = serviceParam.getString("@node"); // 服务节点
		String funcId = serviceParam.getString("@func"); // 服务编号
		String before = serviceParam.getString("@before"); // 前置拦截
		String after = serviceParam.getString("@after"); // 后置拦截

		// 参数校验
		if (StringUtils.isEmpty(funcId) || StringUtils.isEmpty(node)) {
			return ;
		}

		// 获取服务包装对象
		ServiceWrapper wrapper = DbsCache.getService(funcId);
		if (wrapper == null) {
			// 如果不存在，则新建
			wrapper = new ServiceWrapper(funcId, before, after);
			DbsCache.putRemoteService(funcId, wrapper);
		}

		if (DbsCache.getService(funcId).add(node)) {
			String rn = registryNodeThreadLocal.get();
			if (rn == null) {
				// 保存当前注册点的URL
				registryNodeThreadLocal.set(node);
			}
			if (log.isDebugEnabled()) {
				log.info("registry service, from node=" + node + " of service=" + funcId);
			}
		}
	}

	/**
	 * 发布服务
	 * 
	 * @param out 输出对象
	 * @param subscribe 订阅服务（正则表达式）
	 */
	private void publishService(Param out, String subscribe) {
		Param bodyParam = out.getBody();
		String rn = registryNodeThreadLocal.get();
		// 当前节点的所有服务（包含已同步的其它节点服务）
		List<String> allServiceKeys = DbsCache.listServiceKey();
		// 构建同步到master的服务
		for (String sk : allServiceKeys) {
			ServiceWrapper wrapper = DbsCache.getService(sk);
			if (wrapper == null) {
				continue;
			}
			if (StringUtils.isNumeric(sk) && Integer.parseInt(sk) < 10000) {
				// 如果是系统内服务，则不需要返回
				continue;
			}
			if (wrapper.getScope() == Scope.PRIVATE) {
				// 如果服务的作用域是private,　则不需要返回
				continue;
			}
			if (!sk.matches(subscribe)) {
				// 不发布用户未订阅的服务
				continue;
			}
			String nodes = wrapper.format();
			if (nodes.contains(rn)) {
				// 不发布订阅节点本地已有的服务
				continue;
			}
			String[] nodeArr = StringUtils.split(nodes, CharacterUtil.SEPARATOR);
			for (String node : nodeArr) {
				if (StringUtils.isEmpty(node)) {
					continue;
				}
				Param serviceParam = bodyParam.addParam("service");
				serviceParam.setProperty("@node", node);
				serviceParam.setProperty("@func", sk);
				// 服务通过远程调用，增加拦截器无效
				// serviceParam.setProperty("@before", StringUtils.join(wrapper.getBefores(), CharacterUtil.SEPARATOR));
				// serviceParam.setProperty("@after", StringUtils.join(wrapper.getAfters(), CharacterUtil.SEPARATOR));
				// 当serviceNode访问另一网段远程服务时，可能需要通过代理来完成访问。
				// serverServer必须部署在两个网段可访问位置
				serviceParam.setProperty("@proxy", DbsCache.getConst(DbsConst.DBS_LOCAL));
			}
		}
	}


	/**
	 * 发布服务
	 * 
	 * @param out 输出对象
	 */
	private void publishConst(Param out) {
		Param bodyParam = out.getBody();
		List<String> allConstKeys = DbsCache.listConstKey();
		// 构建同步到master的服务
		for (String ck : allConstKeys) {
			Param constParam = bodyParam.addParam("constant");
			constParam.setProperty("@name", ck);
			constParam.setProperty("@value", DbsCache.getConst(ck));
		}
	}

}
