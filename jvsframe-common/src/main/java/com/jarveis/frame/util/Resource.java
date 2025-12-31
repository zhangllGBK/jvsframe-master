package com.jarveis.frame.util;

import java.io.InputStream;
import java.net.URL;

/**
 * 资源类
 * 
 * @author liuguojun
 * 
 */
public class Resource {

	/**
	 * 获取资源的输入流
	 * 
	 * <pre>
	 * InputStream is = Resource.getStream(&quot;demo.txt&quot;);
	 * </pre>
	 * 
	 * @param path
	 *            文件路径
	 * @return InputStream
	 */
	public static InputStream getStream(String path) {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		return classLoader.getResourceAsStream(path);
	}
	
	/**
	 * 获取资源的url
	 * 
	 * <pre>
	 * URL url = Resource.getURL(&quot;demo.txt&quot;);
	 * </pre>
	 * 
	 * @param path
	 *            文件路径
	 * @return URL
	 */
	public static URL getURL(String path) {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		return classLoader.getResource(path);
	}

	/**
	 * 获取当前应用程序的classpath路径
	 * <pre>
	 * String path = Resource.getClasspath();
	 * </pre>
	 * 
	 * @return String
	 */
	public static String getClasspath() {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		URL url = classLoader.getResource("/");
		if (url == null) {
			url = Resource.class.getResource("/");
		}
		return url.getPath();
	}
}
