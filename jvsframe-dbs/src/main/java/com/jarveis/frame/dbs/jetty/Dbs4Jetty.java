package com.jarveis.frame.dbs.jetty;

import com.jarveis.frame.config.ApplicationConfig;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * 使用jetty做为启动服务器
 *
 * @author liuguojun
 */
public class Dbs4Jetty {

    private static final Logger log = LoggerFactory.getLogger(Dbs4Jetty.class);

    private int acceptors = -1; // 线程池大小（接受用户请求）
    private int selectors = -1; // 线程池大小（解析用户请求）
    private int poolSize = 200; // 线程池大小（处理用户请求）
    private int httpPort = 8080; // http端口号
    private String contextPath = "/"; // 请求根路径
    private String staticPath; // 静态文件路径
    private boolean wsEnable = false;


    /**
     * 设置接受用户请求的线程池大小（默认-1，系统分配）
     * acceptor只用于与用户请求建立连接,不做其它事情,建立完连接后,就交给selector进行处理.
     *
     * @param acceptors 用于接受用户请求的线程数
     */
    public void setAcceptors(int acceptors) {
        this.acceptors = acceptors;
    }

    /**
     * 设置解析用户请求的线程池大小（默认-1，系统分配）
     * selector只用于处理HTTP消息协议的包,封包\解包操作,便于业务进行处理.
     *
     * @param selectors 用于解析用户请求的线程数
     */
    public void setSelectors(int selectors) {
        this.selectors = selectors;
    }

    /**
     * 设置处理用户请求的线程池大小（默认200）
     * worker是用于处理具体业务的线程
     *
     * @param poolSize 线程池大小
     */
    public void setPoolSize(int poolSize) {
        this.poolSize = poolSize;
    }

    /**
     * 设置http服务的端口号（默认8080）
     *
     * @param httpPort 端口号
     */
    public void setHttpPort(int httpPort) {
        this.httpPort = httpPort;
    }

    /**
     * 设置用户请求的根路径
     *
     * @param contextPath 请求路径
     */
    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    /**
     * 设置静态文件的目录地址
     *
     * @param staticPath 静态文件目录
     */
    public void setStaticPath(String staticPath) {
        this.staticPath = staticPath;
    }

    /**
     * 设置是否开启websocket
     *
     * @param wsEnable
     */
    public void setWsEnable(boolean wsEnable) {
        this.wsEnable = wsEnable;
    }

    /**
     * 启动服务器
     */
    public void start() {
        try {
            Server server = new Server(buildThreadPool());
            server.addConnector(buildHttpConnector(server));
            server.setHandler(buildContextHandler());

            server.start();
            server.join();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    /**
     * 构建线程池
     *
     * @return QueuedThreadPool
     */
    private QueuedThreadPool buildThreadPool() {
        QueuedThreadPool threadPool = new QueuedThreadPool();
        threadPool.setMaxThreads(poolSize);
        threadPool.setName("DbsService");

        return threadPool;
    }

    /**
     * 构建Http服务器连接器
     *
     * @param server 服务器
     * @return ServerConnector
     * @version jdk_1.8
     */
    private ServerConnector buildHttpConnector(Server server) {
        ServerConnector http = new ServerConnector(server, acceptors, selectors);
        http.setPort(httpPort);
        //
        http.setIdleTimeout(30000);

        return http;
    }

    /**
     * 构建请求处理器
     *
     * @return ContextHandler
     */
    private Handler buildContextHandler() throws IOException {
        ContextHandlerCollection handlers = new ContextHandlerCollection();

        if (StringUtils.isNotEmpty(staticPath)) {
            ContextHandler context = new ContextHandler();

            ResourceHandler resourceHandler = new ResourceHandler();
            resourceHandler.setDirectoriesListed(true);

            context.setContextPath(contextPath);
            context.setHandler(resourceHandler);
            context.setBaseResource(Resource.newResource(staticPath));

            handlers.addHandler(context);
        }

        if (wsEnable) {
            // 开启WebSocket
            ContextHandler context = new ContextHandler();
            context.setContextPath("/ws");
            context.setHandler(new DbsWebSocketHandler());
            handlers.addHandler(context);
        }

        ContextHandler context = new ContextHandler();
        context.setContextPath(contextPath);
        context.setHandler(new DbsHandler());
        handlers.addHandler(context);

        return handlers;
    }

    public static void main(String[] args) {
        ApplicationConfig config = new ApplicationConfig();
        config.parse();
        config.execute();

        String acceptors = StringUtils.defaultIfEmpty(System.getProperty("dbs.acceptors"), "-1");
        String selectors = StringUtils.defaultIfEmpty(System.getProperty("dbs.selectors"), "-1");
        String poolSize = StringUtils.defaultIfEmpty(System.getProperty("dbs.poolSize"), "200");
        String httpPort = StringUtils.defaultIfEmpty(System.getProperty("dbs.httpPort"), "8080");
        String contextPath = StringUtils.defaultIfEmpty(System.getProperty("dbs.contextPath"), "/");
        String staticPath = StringUtils.defaultIfEmpty(System.getProperty("dbs.staticPath"), "static");
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
                "8888888b.  888888b.    .d8888b.  \n" +
                "888  \"Y88b 888  \"88b  d88P  Y88b \n" +
                "888    888 888  .88P  Y88b.      \n" +
                "888    888 8888888K.   \"Y888b.   \n" +
                "888    888 888  \"Y88b     \"Y88b. \n" +
                "888    888 888    888       \"888 \n" +
                "888  .d88P 888   d88P Y88b  d88P \n" +
                "8888888P\"  8888888P\"   \"Y8888P\"  \n" +
                "                                 \n");

        jetty.start();
    }
}
