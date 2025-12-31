package com.jarveis.frame.config;

/**
 * 配置文件解析接口
 *
 * @author liuguojun
 * @since 2018-02-22
 */
public interface ConfigParser {

	/**
	 * 解析默认的配置文件
	 */
	public void parse();
	
	/**
	 * 解析配置文件
	 * 
	 * @param filePath
	 */
	public void parse(String filePath);
}
