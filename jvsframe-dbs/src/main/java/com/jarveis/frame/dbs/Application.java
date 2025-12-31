package com.jarveis.frame.dbs;

import com.jarveis.frame.config.ApplicationConfig;
import com.jarveis.frame.config.ClassScanner;
import com.jarveis.frame.config.ModuleParser;
import com.jarveis.frame.config.ParserBean;
import com.jarveis.frame.dbs.ant.DbsApplication;
import com.jarveis.frame.dbs.ant.DbsServer;
import com.jarveis.frame.dbs.jetty.Dbs4Jetty;
import com.jarveis.frame.util.CharacterUtil;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * 注解应用程序入口
 *
 * @author liuguojun
 * @since 2022-06-29
 */
public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    /**
     * 应用启动
     *
     * @param clazz 启动类
     * @param args 启动参数
     */
    public static void run(Class<?> clazz, String[] args) {
        DbsApplication application = clazz.getAnnotation(DbsApplication.class);
        if (application != null) {
            String scanPackage = application.scanPackage();
            String[] arr = StringUtils.split(scanPackage, CharacterUtil.SEPARATOR);
            for (String str : arr) {
                ClassScanner.addPackage(str);
            }

            ApplicationConfig config = new ApplicationConfig();
            // 从配置文件中加载解析器
            config.parse();
            // 从注解中加载解析器
            Set<String> classSet = ClassScanner.getClassSet();
            for (String className : classSet) {
                ParserBean parserBean = buildParser(className);
                if (parserBean != null) {
                    config.addParser(parserBean);
                }
            }
            // 运行解析器
            config.execute();
        }

        DbsServer server = clazz.getAnnotation(DbsServer.class);
        if (server != null) {
            Dbs4Jetty jetty = buildServer(server);
            jetty.start();
        }
    }

    /**
     * 构建解析器
     *
     * @param className 类名
     * @return ParserBean
     */
    private static ParserBean buildParser(String className){
        ParserBean parserBean = null;
        try {
            Class<?> objClass = Class.forName(className);
            ModuleParser parser = objClass.getAnnotation(ModuleParser.class);
            if (parser != null) {
                parserBean = new ParserBean(className);
                parserBean.setFile(parser.file());
                parserBean.setRefreshLoad(parser.refreshLoad());
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }

        return parserBean;
    }

    /**
     * 构建服务器
     *
     * @return Dbs4Jetty
     */
    private static Dbs4Jetty buildServer(DbsServer server){
        String acceptors = StringUtils.defaultIfEmpty(System.getProperty("dbs.acceptors"), String.valueOf(server.acceptors()));
        String selectors = StringUtils.defaultIfEmpty(System.getProperty("dbs.selectors"), String.valueOf(server.selectors()));
        String poolSize = StringUtils.defaultIfEmpty(System.getProperty("dbs.poolSize"), String.valueOf(server.poolSize()));
        String httpPort = StringUtils.defaultIfEmpty(System.getProperty("dbs.httpPort"), String.valueOf(server.httpPort()));
        String contextPath = StringUtils.defaultIfEmpty(System.getProperty("dbs.contextPath"), server.contextPath());
        String staticPath = StringUtils.defaultIfEmpty(System.getProperty("dbs.staticPath"), server.staticPath());
        String wsEnable = System.getProperty("dbs.wsEnable");

        Dbs4Jetty jetty = new Dbs4Jetty();
        jetty.setHttpPort(NumberUtils.toInt(httpPort));
        jetty.setContextPath(contextPath);
        jetty.setStaticPath(staticPath);
        jetty.setAcceptors(NumberUtils.toInt(acceptors));
        jetty.setSelectors(NumberUtils.toInt(selectors));
        jetty.setPoolSize(NumberUtils.toInt(poolSize));
        if (StringUtils.isNotEmpty(wsEnable)) {
            jetty.setWsEnable(BooleanUtils.toBoolean(wsEnable));
        }

        log.info("server[httpPort=" + httpPort + ", contextPath=" + contextPath + ", staticPath=" + staticPath + ", poolSize=" + poolSize + "]");

        log.info("\n" +
                "        8888888b.  888888b.    .d8888b.  \n" +
                "        888   Y88b 888   88b  d88P  Y88b \n" +
                "        888    888 888  .88P  Y88b.      \n" +
                "        888    888 8888888K.    Y888b.   \n" +
                "        888    888 888   Y88b      Y88b. \n" +
                "        888    888 888    888        888 \n" +
                "        888  .d88P 888   d88P Y88b  d88P \n" +
                "        8888888P   8888888P     Y8888P     ");

        return jetty;
    }
}
