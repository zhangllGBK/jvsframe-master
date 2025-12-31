package com.jarveis.frame.dbs;

import com.jarveis.frame.bean.ReflectionUtils;
import com.jarveis.frame.config.ClassScanner;
import com.jarveis.frame.config.ConfigParser;
import com.jarveis.frame.config.ModuleParser;
import com.jarveis.frame.dbs.ant.Scope;
import com.jarveis.frame.dbs.bean.FilterBean;
import com.jarveis.frame.dbs.filter.Filter;
import com.jarveis.frame.dbs.task.ServiceRegistry;
import com.jarveis.frame.dbs.task.ServiceSelfCheck;
import com.jarveis.frame.util.CharacterUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Enumeration;

/**
 * DB上下文
 *
 * @author liuguojun
 * @since 2018-03-23
 */
@ModuleParser
public class DbsParser implements ConfigParser {

    private static final Logger log = LoggerFactory.getLogger(DbsParser.class);

    public static final String CONFIG_PATH = "dbs.xml";

    private ServiceRegistry serviceRegistry = null;
    private ServiceSelfCheck serviceSelfCheck = null;

    /**
     * 解析默认的配置文件
     */
    public void parse() {
        try {
            loadStartupProperty();
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
        try {
            File file = new File(filePath);
            if (file.exists() && file.isFile()) {
                if (log.isDebugEnabled()) {
                    log.debug("file1=" + file);
                }
				Document document = Jsoup.parse(new FileInputStream(file), CharacterUtil.UTF8, "", Parser.xmlParser());
				parse(document);
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("file2=" + filePath);
                }
                Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources(filePath);
				while (urls != null && urls.hasMoreElements()) {
					URL url = urls.nextElement();
                    Document document = Jsoup.parse(url.openStream(), CharacterUtil.UTF8, "", Parser.xmlParser());
                    parse(document);
				}
            }

            DbsContext.init();
            startServiceRegistryTask();
            startServiceSelfCheckTask();
        } catch (Exception ex) {
            log.error("解析" + filePath + "异常", ex);
        }
    }

    /**
     * 加载开启系统时的配置信息
     */
    private void loadStartupProperty(){
        // 节点类型
        DbsCache.putConst(DbsConst.DBS_NODE, System.getProperty("dbs.node"));
        // 版本号
        DbsCache.putConst(DbsConst.DBS_VERSION, System.getProperty("dbs.version"));
        // 机器码
        DbsCache.putConst(DbsConst.DBS_MACHINE, System.getProperty("dbs.machine"));
        // 本地服务路径
        DbsCache.putConst(DbsConst.DBS_LOCAL, System.getProperty("dbs.local"));
    }

    /**
     * 解析xml文档
     *
     * @param document 文档对象
     */
    private void parse(Document document) {
        if (document == null) {
            return;
        }

        // 记录时间戳
        Element dbs = document.select("dbs").get(0);
        String timestamp = dbs.attr("timestamp");
        DbsCache.compareTimestamp(timestamp);

        // 初始化常量配置集合
        Elements constants = document.select("constants > constant");
        for (Element constant : constants) {
            parseConstant(constant);
        }
        // 初始化过滤配置集合
        Elements filters = document.select("filters > filter");
        for (Element filter : filters) {
            parseFilter(filter);
        }
        // 初始化功能配置集合
        Elements functions = document.select("functions > function");
        for (Element function : functions) {
            parseFunction(function);
        }
    }

    /**
     * 解析常量配置
     *
     * @param element 文档元素
     */
    private void parseConstant(Element element) {
        String name = element.attr("name");
        String value = element.attr("value");

        if (DbsConst.DBS_SCAN_PACKAGE.equals(name)) {
            ClassScanner.addPackage(value);
        } else {
            DbsCache.putConst(name, value);
        }
    }

    /**
     * 解析功能配置
     *
     * @param element 文档元素
     */
    private void parseFunction(Element element) {
     
        String code = element.attr("code"); // 服务编号
        //String init = element.attr("init"); // 初始化方式
        //String desc = element.attr("desc"); // 描述
        String clazz = element.attr("clazz"); // 服务源class
        String transaction = element.attr("transaction"); // 事务支持
        String scope = element.attr("scope"); // 作用域

        if (StringUtils.isEmpty(transaction)) {
            transaction = "false";
        }
        if (StringUtils.isEmpty(clazz)) {
            log.error(code + "未定义clazz属性");
            return ;
        }
        Object service = ReflectionUtils.newInstance(clazz);
        if (service == null) {
            log.error(clazz + "未能实例化！");
            return ;
        }
        if (!(service instanceof Service)) {
            log.error(clazz + "未实现 com.jarveis.dbs.core.Service接口");
            return ;
        }

        // 当前服务的节点类型,默认为"node"
        String nodeType = StringUtils.defaultIfEmpty(DbsCache.getConst(DbsConst.DBS_NODE), DbsConst.DBS_NODE_SERVICE);
        // dbs服务的作用域
        Scope scope1 = Scope.PRIVATE.toString().equalsIgnoreCase(scope) ? Scope.PRIVATE : Scope.PUBLIC;
        if (DbsConst.DBS_NODE_SERVICE.equals(nodeType)) {
            if (DbsUtils.isFuncId(code) > 0) {
                // 获取缓存中的服务包装类
                ServiceWrapper wrapper = new ServiceWrapper(code);
                wrapper.setTransaction(Boolean.parseBoolean(transaction));
                wrapper.setScope(scope1);
                wrapper.add((Service) service);
                DbsCache.putLocalService(code, wrapper);
            }
        } else {
            if (DbsUtils.isFuncId(code) == 0) {
                // 获取缓存中的服务包装类
                ServiceWrapper wrapper = new ServiceWrapper(code);
                wrapper.setTransaction(Boolean.parseBoolean(transaction));
                wrapper.setScope(scope1);
                wrapper.add((Service) service);
                DbsCache.putLocalService(code, wrapper);
            }
        }

    }

    /**
     * 解析过滤器配置
     *
     * @param element 拦截器配置节点
     */
    private void parseFilter(Element element) {
        FilterBean filterBean;
        String id = element.attr("id");
        String clazz = element.attr("clazz");
        String type = element.attr("type");
        String match = element.attr("match");

        if (StringUtils.isEmpty(clazz)) {
            log.error(id + "未定义clazz属性");
            return ;
        }

        Object filter = ReflectionUtils.newInstance(clazz);
        if (filter == null) {
            log.error(clazz + "未能实例化！");
            return ;
        }
        if (!(filter instanceof Filter)) {
            log.error(clazz + "未实现 com.jarveis.frame.dbs.filter.Filter接口");
            return ;
        }

        // 添加缓存
        FilterWrapper fw = new FilterWrapper(id, (Filter)filter);
        DbsCache.putFilter(fw.getCode(), fw);

        // 初始化过滤器
        fw.get().init();

        if (DbsCache.getFilter(id) == null) {
            filterBean = new FilterBean();
            filterBean.setId(id);
            filterBean.setClazz(clazz);
            filterBean.setType(type);
            filterBean.setMatch(match);

            DbsCache.addFilterBean(filterBean);
        }
    }

    /**
     * 开启注册服务的任务
     */
    private void startServiceRegistryTask() {
        String nodeType = StringUtils.defaultIfEmpty(DbsCache.getConst(DbsConst.DBS_NODE), DbsConst.DBS_NODE_SERVICE);
        String serviceServer = DbsCache.getConst(DbsConst.DBS_SERVICE_SERVER); //　service服务器
        String localNode = DbsCache.getConst(DbsConst.DBS_LOCAL); // 当前服务地址
        String heartBeatTime = DbsCache.getConst(DbsConst.DBS_HEART_BEAT_TIME); // 同步服务的频率

        if (StringUtils.isNotEmpty(serviceServer) && StringUtils.isNotEmpty(localNode) && DbsConst.DBS_NODE_SERVICE.equals(nodeType)) {

            if (serviceRegistry == null) {
                // cluster（local->remote）监控服务
                serviceRegistry = new ServiceRegistry();
                serviceRegistry.init();
                new Thread(() -> {
                    while (true) {
                        try {
                            serviceRegistry.execute();
                            Thread.sleep(NumberUtils.toInt(heartBeatTime, 10000));
                        } catch (Exception ex) {
                            log.error(ex.getMessage(), ex);
                        }
                    }
                }).start();
            }
        }

    }

    /**
     * 服务自检
     */
    private void startServiceSelfCheckTask() {
        if (serviceSelfCheck == null) {
            serviceSelfCheck = new ServiceSelfCheck();
            serviceSelfCheck.init();
            new Thread(() -> {
                while (true) {
                    try {
                        serviceSelfCheck.execute();
                        Thread.sleep(100);
                    } catch (Exception ex) {
                        log.error(ex.getMessage(), ex);
                    }
                }
            }).start();
        }
    }

}
