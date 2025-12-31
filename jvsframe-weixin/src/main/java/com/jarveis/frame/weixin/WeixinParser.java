package com.jarveis.frame.weixin;

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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * 微信文件解析器
 *
 * @author liuguojun
 * @since 2021-06-09
 */
@ModuleParser
public class WeixinParser implements ConfigParser {

    private static final Logger log = LoggerFactory.getLogger(WeixinParser.class);

    public static final String CONFIG_PATH = "weixin.xml";

    @Override
    public void parse() {
        parse(CONFIG_PATH);
    }

    @Override
    public void parse(String filePath) {
        Document document;
        try {
            File file = new File(filePath);
            if (file.exists() && file.isFile()) {
                document = Jsoup.parse(new FileInputStream(file), CharacterUtil.UTF8, "", Parser.xmlParser());
            } else {
                document = Jsoup.parse(Resource.getStream(filePath), CharacterUtil.UTF8, "", Parser.xmlParser());
            }

            // 解析文档
            parse(document);

            if (log.isDebugEnabled()) {
                log.debug(filePath + "=" + Resource.getURL(filePath).getPath());
            }
        } catch (IOException ex) {
            log.error("解析" + filePath + "异常", ex);
        }
    }

    /**
     * 解析文档
     *
     * @param document 文档对象
     */
    private void parse(Document document) {
        // 记录时间戳
        Element dbs = document.select("weixin").get(0);
        String timestamp = dbs.attr("timestamp");
        WeixinCache.compareTimestamp(timestamp);

        // 初始化常量配置集合
        Elements constants = document.select("constants > constant");
        for (Element constant : constants) {
            parseConstant(constant);
        }
        // 初始化功能配置集合
        Elements keywords = document.select("keywords > keyword");
        for (Element keyword : keywords) {
            parseKeyword(keyword);
        }
    }

    /**
     * 解析常量
     *
     * @param constant 配置节点
     */
    private void parseConstant(Element constant) {
        String name = constant.attr("name");
        String value = constant.attr("value");
        WeixinCache.putConstant(name, value);
    }

    /**
     * 解析关键字
     *
     * @param keyword 关键字节点
     */
    private void parseKeyword(Element keyword) {
        String name = keyword.attr("name");
        String value = keyword.attr("value");
        WeixinCache.putKeyword(name, value);
    }
}
