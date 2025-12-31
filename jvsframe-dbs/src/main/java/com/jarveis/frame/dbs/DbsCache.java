package com.jarveis.frame.dbs;

import com.jarveis.frame.dbs.bean.FilterBean;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Dbs缓存，包含常量配置、拦截器配置及对象、本地（远程）服务配置及对象
 * @author liuguojun
 * @since  2015-08-09
 */
public class DbsCache {

	private static final Logger log = LoggerFactory.getLogger(DbsCache.class);

	private static long timestamp = 0L;
	private static Map<String, String> dbsConstant = new HashMap<String, String>();
	private static Map<String, FilterWrapper> dbsFilter = new HashMap<String, FilterWrapper>();

	/**
	 * 用于存储可提供的服务
	 */
	private static ConcurrentMap<String, ServiceWrapper> localService = new ConcurrentHashMap<String, ServiceWrapper>();

	private static ConcurrentMap<String, ServiceWrapper> remoteService = new ConcurrentHashMap<String, ServiceWrapper>();

	/**
	 * 存储服务设置的过滤器
	 */
    private static ArrayList<FilterBean> filterBeans = new ArrayList<FilterBean>();

	/**
	 * 存领上传文件
	 */
	private static final ThreadLocal<List<FileItem>> uploadThreadLocal = new ThreadLocal<List<FileItem>>();

	/**
	 * 存储输入流
	 */
	private static final ThreadLocal<byte[]> streamThreadLocal = new ThreadLocal<byte[]>();

	/**
	 * 比较时间戳, 如果传入的时间比系统时间大，则返回true(并重置系统时间为传入时间)，否则返回false；时间单位：ms
	 *
	 * @param str
	 * @return
	 * @since 2020-06-17
	 */
	public static boolean compareTimestamp(String str) {
		long temp = NumberUtils.toLong(str, 0L);
		if (timestamp < temp) {
			timestamp = temp;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 添加常量
	 * 
	 * @param name 常量名称
	 * @param value 常量值
	 */
	public static void putConst(String name, String value) {
		String obj = getConst(name);
		if (obj == null) {
			dbsConstant.put(name, value);
			log.info("constant name=" + name + ", value=" + value);
		}
	}

	/**
	 * 获取常量值
	 * 
	 * @param name 常量名称
	 * @return
	 */
	public static String getConst(String name) {
		return dbsConstant.get(name);
	}


	/**
	 * 获取常量定义列表
	 * 
	 * @return
	 */
	public static List<String> listConstKey() {
		List<String> list = new ArrayList<String>();

		Set<String> s = dbsConstant.keySet();
		Iterator<String> e = s.iterator();
		while (e.hasNext()) {
			list.add(e.next());
		}

		return list;
	}

	/**
	 * 添加Filter
	 * 
	 * @param id 过滤器标识
	 * @param fw 过滤器对象
	 */
	public static void putFilter(String id, FilterWrapper fw) {
		dbsFilter.put(id, fw);

		log.info("filterId=" + id + ", class=" + fw.get());
	}

	/**
	 * 获取Filter
	 * 
	 * @param id 过滤器标识
	 * @return Filter
	 */
	public static FilterWrapper getFilter(String id) {
		if (StringUtils.isNoneEmpty(id)) {
			return dbsFilter.get(id);
		}
		return null;
	}

    /**
     * 添加filter配置
	 *
     * @param filterBean
     */
	public static void addFilterBean(FilterBean filterBean){
	    if (filterBean != null && !filterBeans.contains(filterBean)) {
            filterBeans.add(filterBean);
        }
    }

    /**
     * 获取filter配置列表
	 *
     * @return
     */
    public static List<FilterBean> getFilterBeans(){
	    return filterBeans;
    }

	/**
	 * 获取Servcie
	 * 
	 * @param funcId 服务编号
	 * @return
	 */
	public static ServiceWrapper getService(String funcId) {
		ServiceWrapper sw = null;
		if (StringUtils.isNotEmpty(funcId)) {
			sw = localService.get(funcId);
			if (sw == null) {
				sw = remoteService.get(funcId);
			}
		}
		return sw;
	}

	/**
	 * 设置本地Service
	 * 
	 * @param funcId 服务编号
	 * @param serviceWrapper 服务包装类
	 */
	public static void putLocalService(String funcId, ServiceWrapper serviceWrapper) {
		if (serviceWrapper != null) {
			localService.put(funcId, serviceWrapper);
			log.info("add funcId=" + funcId + ", class=" + serviceWrapper.get());
		} else {
			localService.remove(funcId);
			log.info("remove funcId=" + funcId);
		}
	}

	/**
	 * 设置远程Service
	 * 
	 * @param funcId 服务编号
	 * @param serviceWrapper 服务包装类
	 */
	public static void putRemoteService(String funcId, ServiceWrapper serviceWrapper) {
		if (serviceWrapper != null) {
			remoteService.put(funcId, serviceWrapper);
			log.info("add funcId=" + funcId + ", class=" + serviceWrapper.get());
		} else {
			remoteService.remove(funcId);
			log.info("remove funcId=" + funcId);
		}
	}

	/**
	 * 设置远程Service
	 * 
	 * @param serviceMap
	 */
	public static void putRemoteService(Map<String, ServiceWrapper> serviceMap) {
		remoteService.putAll(serviceMap);
	}

	/**
	 * 获取服务编码列表
	 * 
	 * @return
	 */
	public static List<String> listServiceKey() {
		List<String> list = new ArrayList<String>();

		Set<String> s = localService.keySet();
		Iterator<String> e = s.iterator();
		while (e.hasNext()) {
			list.add(e.next());
		}

		s = remoteService.keySet();
		e = s.iterator();
		while (e.hasNext()) {
			list.add(e.next());
		}

		return list;
	}

	/**
	 * 获取上传的文件
	 * 
	 * @return List<FileItem>
	 */
	public static synchronized List<FileItem> getUploads() {
		List<FileItem> files = uploadThreadLocal.get();
		uploadThreadLocal.remove();
		return files;
	}

	/**
	 * 设置上传文件
	 * 
	 * @param files
	 */
	public static synchronized void setUploads(List<FileItem> files) {
		uploadThreadLocal.set(files);
	}

	/**
	 * 获取输入流
	 * 
	 * @return byte[]
	 */
	public static synchronized byte[] getStream() {
		byte[] bytes = streamThreadLocal.get();
		streamThreadLocal.remove();
		return bytes;
	}

	/**
	 * 设置输入流
	 * 
	 * @param
	 */
	public static synchronized void setStream(byte[] bytes) {
		streamThreadLocal.set(bytes);
	}
}
