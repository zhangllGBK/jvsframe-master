package com.jarveis.frame.config;

/**
 * 配置文件解析代理
 *
 * @author liuguojun
 * @since 2018-02-22
 */
public class ConfigProxy {

	/**
	 * 代理执行
	 * 
	 * @param parser
	 *            解析器
	 * @throws Exception
	 * @see {@link com.jarveis.frame.config.ConfigParser#parse()}
	 */
	public static void parse(ConfigParser parser) {
		parser.parse();
	}

	/**
	 * 代理执行
	 * 
	 * @param parser
	 *            解析器
	 * @param file
	 *            解析器要解析的配置文件
	 * @throws Exception
	 * @see {@link com.jarveis.frame.config.ConfigParser#parse(String)}
	 */
	public static void parse(ConfigParser parser, String file) {
		parser.parse(file);
	}
}
