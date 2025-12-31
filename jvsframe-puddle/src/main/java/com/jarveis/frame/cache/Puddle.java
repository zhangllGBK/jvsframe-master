package com.jarveis.frame.cache;

import com.jarveis.frame.cache.broadcast.NoneCacheChannel;
import com.jarveis.frame.cache.broadcast.RedisCacheChannel;
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
 * Puddle配置解析类
 * 
 * @author liuguojun
 * @since 2018-08-31
 */
@ModuleParser
public class Puddle implements ConfigParser {

	private static final Logger log = LoggerFactory.getLogger(Puddle.class);

	public static final String CONFIG = "config.xml";
	private static CacheChannel channel;

	public static CacheChannel getChannel() {
		return channel;
	}

	public void parse() {
		parse(CONFIG);
	}

	public void parse(String filePath) {
		try {
			if (PuddleConfig.getCacheConfig() == null) {
				Document document = Jsoup.parse(Resource.getStream(filePath), CharacterUtil.UTF8, "", Parser.xmlParser());
				this.parseJvsCache(document);
				this.parseRedis(document);
				this.parsePuddle(document);
			}
			
			String broadcast = PuddleConfig.getCacheConfig().getProperty("broadcast");
			if ("redis".equalsIgnoreCase(broadcast)) {
				channel = RedisCacheChannel.getInstance();
			} else if ("none".equalsIgnoreCase(broadcast)) {
				channel = NoneCacheChannel.getInstance();
			} else {
				throw new CacheException("Cache Channel not defined. name = " + broadcast);
			}
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
		}
	}

	/**
	 * 解析redis配置
	 * 
	 * @param document
	 */
	private void parseRedis(Document document) {
		Elements dss = document.select("redisConfig > datasource");
		for (Element ds : dss) {
			parseRedis(ds);
		}
	}

	/**
	 * 解析redis配置
	 * 
	 * @param element
	 */
	private void parseRedis(Element element) {
		Properties redisConfig = new Properties();

		redisConfig.setProperty("id", element.attr("id"));
		redisConfig.setProperty("default", element.attr("default"));
		redisConfig.setProperty("mode", element.attr("mode"));

		Elements pps = element.select("property");
		for (Element pp : pps) {
			String name = pp.attr("name");
			String value = pp.attr("value");
			redisConfig.put(name, value);
		}

		PuddleConfig.setRedisConfig(redisConfig);
	}

	/**
	 * 解析redis配置
	 *
	 * @param document
	 */
	private void parseJvsCache(Document document) {
		Elements dss = document.select("jvsCacheConfig > datasource");
		for (Element ds : dss) {
			parseJvsCache(ds);
		}
	}

	/**
	 * 解析redis配置
	 *
	 * @param element
	 */
	private void parseJvsCache(Element element) {
		Properties jvsCacheConfig = new Properties();

		jvsCacheConfig.setProperty("mode", element.attr("mode"));

		Elements pps = element.select("property");
		for (Element pp : pps) {
			String name = pp.attr("name");
			String value = pp.attr("value");
			jvsCacheConfig.put(name, value);
		}

		PuddleConfig.setJvsCacheConfig(jvsCacheConfig);
	}

	/**
	 * 解析puddle配置
	 * 
	 * @param document
	 */
	private void parsePuddle(Document document) {
		Elements eles = document.select("puddleConfig > puddle");
		for (Element ele : eles) {
			parsePuddle(ele);
		}
	}

	/**
	 * 解析puddle配置
	 * 
	 * @param element
	 * @throws Exception
	 */
	private void parsePuddle(Element element) {
		Properties cacheConfig = new Properties();

		Elements pps = element.select("property");
		for (Element pp : pps) {
			String name = pp.attr("name");
			String value = pp.attr("value");
			cacheConfig.put(name, value);
		}

		PuddleConfig.setCacheConfig(cacheConfig);
	}

}
