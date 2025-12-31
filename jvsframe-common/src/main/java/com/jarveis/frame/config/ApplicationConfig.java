package com.jarveis.frame.config;

import com.jarveis.frame.bean.ReflectionUtils;
import com.jarveis.frame.util.CharacterUtil;
import com.jarveis.frame.util.Resource;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;

/**
 * 应用配置
 *
 * @author liuguojun
 * @since 2014-12-23
 */
public class ApplicationConfig {

	private static final Logger log = LoggerFactory.getLogger(ApplicationConfig.class);

	private static final String CONFIG_PATH = "config.xml";

	private final ArrayList<ParserBean> parserList = new ArrayList<>();

	private final String[] defaultParsers = {
			"com.jarveis.frame.cache.Puddle",
			"com.jarveis.frame.jdbc.JdbcParser",
			"com.jarveis.frame.dbs.DbsParser",
			"com.jarveis.frame.task.TaskParser"
	};

	/**
	 * 构造方法
	 */
	public ApplicationConfig(){
	}

	/**
	 * 解析默认的配置文件
	 */
	public void parse() {
		try {
			parse(CONFIG_PATH);
		} catch (Exception ex) {
			log.error("加载数据源出错！", ex);
		}
	}

	/**
	 * 解析配置文件
	 * 
	 * @param filePath 配置文件
	 */
	public void parse(String filePath) {
		Document document = null;
		try {
			document = Jsoup.parse(Resource.getStream(filePath), CharacterUtil.UTF8, "", Parser.xmlParser());
			log.info(filePath + "=" +document.html());
		} catch (Exception ex) {
			log.error("解析" + filePath + "异常", ex);
		}

		if (document != null) {
			// 初始化过滤配置集合
			Elements eles = document.select("module > parser");
			for (Element ele : eles) {
				String clazz = ele.attr("clazz");
				String file = ele.attr("file");
				String dynamicRefresh = ele.attr("dynamic-refresh");
				if (StringUtils.isEmpty(clazz)) {
					log.error("parser未定义clazz属性");
					continue;
				}
				this.addParser(clazz, file, dynamicRefresh);
			}
		}
	}

	/**
	 * 执行parser
	 */
	public void execute(){
		// 排序，调整解析器的执行顺序
		Collections.sort(parserList);
		for (ParserBean bean : parserList) {
			String clazz = bean.getClazz();
			Object object = ReflectionUtils.newInstance(clazz);
			if (object == null) {
				log.error(clazz + "未能实例化！");
				continue;
			}
			if (!(object instanceof ConfigParser)) {
				log.error(clazz + "未实现 com.jarveis.frame.config.ConfigParser接口");
				continue;
			}

			String file = bean.getFile();
			ConfigParser parser = (ConfigParser) object;
			if (StringUtils.isEmpty(file)) {
				ConfigProxy.parse(parser);
			} else {
				ConfigProxy.parse(parser, file);
			}
		}
	}

	/**
	 * 添加parser配置对象
	 *
	 * @param parserBean 解析器配置对象
	 */
	public void addParser(ParserBean parserBean){
		if (!parserList.contains(parserBean)) {
			for(int i = 0; i < defaultParsers.length; i++) {
				if (defaultParsers[i].equals(parserBean.getClazz())) {
					// 设置解析器的优先级，从小到大；越小越优先
					parserBean.setOrder(i);
				}
			}
			parserList.add(parserBean);
		}
	}

	/**
	 * 添加parser配置
	 *
	 * @param parserClass 解析器类名
	 * @param file        文件
	 * @param refreshLoad 是否刷新
	 */
	public void addParser(String parserClass, String file, String refreshLoad){
		ParserBean parserBean = new ParserBean(parserClass);
		if (StringUtils.isNotEmpty(file)) {
			parserBean.setFile(file);
		}
		if (StringUtils.isNotEmpty(refreshLoad)) {
			parserBean.setRefreshLoad(Boolean.parseBoolean(refreshLoad));
		}
		addParser(parserBean);
	}

}
