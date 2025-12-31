package com.jarveis.frame.config;

import com.jarveis.frame.util.Resource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;

/**
 * 配置文件监视器
 *
 * <pre>
 * 配置文件监视器，监视解析器的配置文件，如果配置文件发生变动，会再次调用解析器去解析配置文件。
 *
 * ConfigParser.parse(filePath)
 *
 * 开启配置文件监视器，需要如下配置：
 *
 * &lt;parser clazz="com.jarveis.frame.dbs.DbsParser" file="dbs.xml" dynamic-refresh="true" /&gt;
 *
 * 默认配置：
 * dynamic-refresh=false;
 *
 * </pre>
 *
 * @author poyexinghun
 * @since 2019-06-21
 */
public class ConfigWatcher implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(ConfigWatcher.class);

    private ConfigParser parser;
    private File file;
    private String jarFilePath;
    private long lastModified = 0L;

    public ConfigWatcher(ConfigParser parser, String filePath) {
        this.parser = parser;
        // 通过文档路径构建文件
        this.file = new File(filePath);
        if (file.exists() && file.isFile()) {
            // 如果文件路径对应的文件存在，则获取文件的最后一次修改时间
            log.info("parser=" + parser.getClass().getName() + ", file=" + filePath);
            this.lastModified = file.lastModified();
        } else {
            // 如果不存在，则在classpath中查找文件是否存在
            URL url = Resource.getURL(filePath);
            if (url != null) {
                if ("jar".equals(url.getProtocol())) {
                    // url = jar:file:/home/shfcoc/programs/demo/lib/node1-0.0.1.jar!/config.xml
                    // path = file:/home/shfcoc/programs/demo/lib/node1-0.0.1.jar!/config.xml
                    // 文件存在于jar包中
                    String path = url.getPath();
                    filePath = path.substring(0, path.indexOf('!'));
                    jarFilePath = path.substring(path.indexOf('!') + 1);
                    filePath = filePath.replace("file:", StringUtils.EMPTY);
                } else {
                    // 文件存在于classes中
                    filePath = url.getPath();
                }
            }
        }

        if (lastModified == 0L) {
            // 当通过路径获取文件不存在时，再查询classpath获取的文件路径
            log.info("filePath=" + filePath);
            this.file = new File(filePath);
            if (file.exists() && file.isFile()) {
                this.lastModified = file.lastModified();
                log.info("parser=" + parser.getClass().getName() + ", file=" + filePath);
            }
        } else {
            this.file = null;
        }

    }

    @Override
    public void run() {
        boolean isloop = true;
        while (isloop && file != null) {
            long modified = file.lastModified();
            if (log.isDebugEnabled()) {
                log.debug(file.getPath() + " lastModifed=" + lastModified + ", modified=" + modified);
            }
            if (modified > lastModified) {
                String filePath = file.getPath();
                if (StringUtils.isNotEmpty(jarFilePath)) {
                    // jar:file:/home/shfcoc/programs/demo/lib/node1-0.0.1.jar!/config.xml
                    filePath = "jar:file:" + filePath + "!" + jarFilePath;
                }
                parser.parse(filePath);
                lastModified = modified;
            }
            try {
                Thread.sleep(1000L);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

}
