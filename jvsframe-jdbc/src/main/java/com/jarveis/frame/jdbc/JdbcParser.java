package com.jarveis.frame.jdbc;

import com.alibaba.druid.pool.DruidDataSource;
import com.jarveis.frame.config.ConfigParser;
import com.jarveis.frame.config.ModuleParser;
import com.jarveis.frame.util.CharacterUtil;
import com.jarveis.frame.util.Resource;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * Jdbc配置类
 * 
 * @author liuguojun
 */
@ModuleParser
public class JdbcParser implements ConfigParser {

	private static final Logger log = LoggerFactory.getLogger(JdbcParser.class);

	public static final String JDBC_CONFIG = "config.xml";

	/**
	 * 初始化配置
	 */
	public void parse() {
		parse(JDBC_CONFIG);
	}

	/**
	 * 初始化配置
	 * 
	 * @param filePath
	 */
	public void parse(String filePath) {
		try {
			parseConfig(filePath);
		} catch (Exception ex) {
			log.error("加载数据源出错！", ex);
		}
	}

	/**
	 * 设置配置文件
	 * 
	 * @param filePath
	 * @throws Exception
	 */
	private void parseConfig(String filePath) throws Exception {
		Document document = Jsoup.parse(Resource.getStream(filePath),
				CharacterUtil.UTF8, "", Parser.xmlParser());
		Elements dss = document.select("jdbcConfig > datasource");
		for (Element ds : dss) {
			parseDataSource(ds);
		}

		Elements sfs = document.select("jdbcConfig > sql-files");
		for (Element sf : sfs) {
			Elements fs = sf.select("file");
			for (Element f : fs) {
				String path = f.attr("path");
				parseSQL(path);
			}
		}
	}

	/**
	 * 解析数据源
	 * 
	 * @param element
	 * @throws Exception
	 */
	private void parseDataSource(Element element) throws Exception {
		Properties prop = new Properties();

		String id = element.attr("id");
		String df = element.attr("default");
		Elements pps = element.select("property");
		for (Element pp : pps) {
			String name = pp.attr("name");
			String value = pp.attr("value");
			prop.put(name, value);
			log.info(name + "=" + value);
		}

		DruidDataSource ds = new DruidDataSource();
		ds.configFromPropety(prop);
		JdbcUtil.setDataSource(id, ds, df == null ? false : Boolean.parseBoolean(df));
	}

	/**
	 * 解析SQL配置文件
	 * 
	 * @param filePath
	 * @throws Exception
	 */
	private void parseSQL(String filePath) throws Exception {
		Document document = Jsoup.parse(Resource.getStream(filePath),
				CharacterUtil.UTF8, "", Parser.xmlParser());

		Elements ss = document.select("sql");
		for (Element s : ss) {
			String key = s.attr("id").trim();
			if (key.trim().length() == 0) {
				throw new Exception("sql标签的id属性不能包含违法字符(如空格等字符)");
			}
			String value = s.text();
			JdbcCache.putSQL(key, value);
		}
	}

}
