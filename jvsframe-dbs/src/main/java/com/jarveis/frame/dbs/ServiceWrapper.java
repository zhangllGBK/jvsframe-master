package com.jarveis.frame.dbs;

import com.jarveis.frame.dbs.ant.Scope;
import com.jarveis.frame.dbs.bean.FilterBean;
import com.jarveis.frame.util.CharacterUtil;
import com.jarveis.frame.util.Param;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 服务包装类
 * 
 * @author liuguojun
 * @since 2018-03-08
 */
public class ServiceWrapper {

	private static final Logger log = LoggerFactory.getLogger(ServiceWrapper.class);

	/**
	 * 服务编号
	 */
	private final String funcId;

	/**
	 * 前置拦截
	 */
	private final List<String> beforeList = new ArrayList<>();

	/**
	 * 后置拦截
	 */
	private final List<String> afterList = new ArrayList<>();

	/**
	 * 是否支持事务
	 */
	private boolean transaction;

	/**
	 * 作用域
	 */
	private Scope scope;

	/**
	 * 本地服务
	 */
	private Service localService;

	// 读写分离、访问并发访问对数据修改造成的不一致
	private Map<String, Long> write; // 主机列表
	private String[] read; // 主机列表
	private AtomicInteger count = new AtomicInteger(0);


	/**
	 * 构造方法
	 *
	 * @since 2020-01-06
	 * @param funcId 服务编号
	 */
	public ServiceWrapper(String funcId) {
		this(funcId, null, null);
	}

	/**
	 * 构造方法
	 *
	 * @since 2020-01-06
	 * @param funcId 服务编号
	 * @param before 前置拦截
	 * @param after 后置拦截
	 */
	public ServiceWrapper(String funcId, String before, String after) {
		this.funcId = funcId;
		this.write = new HashMap<>();

		List<FilterBean> filterBeans = DbsCache.getFilterBeans();
		addFilters(after, afterList);
		for (FilterBean filterBean : filterBeans) {
			String id = filterBean.getId(); // 过滤器编码
			String type = filterBean.getType(); // 过滤器类型
			String match = filterBean.getMatch(); // 匹配算法（正则）

			if (!funcId.matches(match)) {
				continue;
			}
			if ("before".equals(type) && !beforeList.contains(id)) {
				beforeList.add(id);
			} else if ("after".equals(type) && !afterList.contains(id)) {
				afterList.add(id);
			}
		}
		addFilters(before, beforeList);

		if (log.isDebugEnabled()) {
			log.debug(String.format("service code=%s, before=%s, after=%s", funcId, ArrayUtils.toString(beforeList), ArrayUtils.toString(afterList)));
		}
	}

	/**
	 * 获取服务编号
	 *
	 * @since 2020-01-06
	 * @return 返回服务编号
	 */
	public String getFuncId() {
		return funcId;
	}

	/**
	 * 添加拦截器（从配置中读取并添加到对应的拦截器集合中）
	 *
	 * @since 2020-01-06
	 * @param filters 拦截器配置
	 * @param list 拦截器集合
	 */
	private void addFilters(String filters, List<String> list) {
		String str = StringUtils.defaultString(filters, StringUtils.EMPTY);
		String[] arr = StringUtils.split(str, CharacterUtil.SEPARATOR);
		for (String t : arr) {
			if (!list.contains(t)) {
				list.add(t);
			}
		}
 	}

	/**
	 * 前置拦截器
	 *
	 * @since 2020-01-06
	 * @return 返回前置拦截器列表
	 */
 	public List<String> getBefores(){
		return beforeList;
	}

	/**
	 * 后置拦截器
	 *
	 * @since 2020-01-06
	 * @return 返回后置拦截器列表
	 */
	public List<String> getAfters(){
		return afterList;
	}

	/**
	 * 添加本地服务
	 * 
	 * @param service 本地服务对象
	 */
	public boolean add(Service service) {
		boolean result = false;

		if (!isLocalService()) {
			localService = service;
			result = true;
		}

		return result;
	}

	/**
	 * 添加远程服务
	 * 
	 * @param service 远程服务地址
	 * @return 添加完成状态(成功，失败)
	 */
	public synchronized boolean add(String service) {
		boolean result = false;

		if (!isLocalService() && StringUtils.isNotEmpty(service)) {
			// 当前时间
			long currentTime = Calendar.getInstance().getTimeInMillis();
			// 设置更新时间
			write.put(service, currentTime);
			read = write.keySet().toArray(new String[] {});
			result = true;

			if (log.isDebugEnabled()) {
				log.debug("services=" + StringUtils.join(read, ";"));
			}
		}

		return result;
	}

	/**
	 * 获取服务
	 * 
	 * @return 返回(本地or远程)服务
	 */
	public Object get() {
		if (isLocalService()) {
			return localService;
		}

		String host = null;
		if (!isEmpty()) {
			if (count.get() > Integer.MAX_VALUE - 1) {
				count.set(0);
			}
			host = read[count.incrementAndGet() % read.length];
			if (log.isDebugEnabled()) {
				log.debug("call service : " + host);
			}
		}

		return host;
	}

	/**
	 * 获取服务
	 * 
	 * @see {@link com.jarveis.frame.dbs.ServiceProxy#callService(String, Param)}
	 * @deprecated
	 * @return 返回(本地or远程)服务
	 */
	public Object getService() {
		return get();
	}

	/**
	 * 删除提供服务的地址
	 * 
	 * @param service 删除远程服务地址
	 * @return 删除服务状态（成功or失败）
	 */
	public boolean remove(String service) {
		if (!isLocalService()) {
			write.remove(service);
			read = write.keySet().toArray(new String[] {});

			if (log.isDebugEnabled()) {
				log.debug("services=" + StringUtils.join(read, ";"));
			}
			return true;
		}
		return false;
	}

	/**
	 * 服务是否为空
	 * 
	 * @return 远程服务是否存在
	 */
	public boolean isEmpty() {
		return write.isEmpty();
	}

	/**
	 * 是否为本地服务
	 * 
	 * @return
	 */
	public boolean isLocalService() {
		return (localService != null);
	}

	/**
	 * 是否开启事务
	 * 
	 * @return
	 */
	public boolean isTransaction() {
		return transaction;
	}

	/**
	 * 设置事务
	 * 
	 * @param transaction
	 */
	public void setTransaction(boolean transaction) {
		this.transaction = transaction;
	}

	/**
	 * 服务作用域
	 * 
	 * @return
	 */
	public Scope getScope() {
		return scope;
	}

	/**
	 * 服务作用域
	 * 
	 * @param scope
	 */
	public void setScope(Scope scope) {
		this.scope = scope;
	}

	/**
	 * 格式化
	 * 
	 * @return
	 */
	public String format() {
		if (isLocalService()) {
			return localService.getClass().getName();
		} else {
			return StringUtils.join(read, CharacterUtil.SEPARATOR);
		}
	}

	/**
	 * 检查远程服务
	 *
	 * @param currentTime 比对时间
	 * @param selfCheckTime 自检时间
	 */
	public void checkRemoteService(long currentTime, long selfCheckTime){
		if (selfCheckTime > 0 && read != null) {
			boolean isChange = false;
			List<String> keyList = new ArrayList<>(read.length);
			for (String key : read) {
				Long lastTime = write.get(key);
				if (currentTime - lastTime <= selfCheckTime) {
					keyList.add(key);
				} else {
					isChange = true;
					write.remove(key);
					if (log.isDebugEnabled()) {
						log.debug(String.format("remove remote service: %s (%s)", funcId, key));
					}
				}
			}
			if (isChange) {
				read = keyList.toArray(new String[]{});
			}
		}
	}
}
