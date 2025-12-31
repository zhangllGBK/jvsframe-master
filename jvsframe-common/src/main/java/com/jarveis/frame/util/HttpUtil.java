package com.jarveis.frame.util;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentProvider;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.*;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.util.Fields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Http工具类
 *
 * @author liuguojun
 * @since 2014-07-31
 */
public final class HttpUtil {

    private static final Logger log = LoggerFactory.getLogger(HttpUtil.class);

    public static int DEFAULT_TIMEOUT = 5000;
    private static final HttpClient httpClient;

    static {
        httpClient = new HttpClient();
        try {
            httpClient.start();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    /**
     * 执行get方法
     *
     * @param url 请求链接
     * @return 响应字符串
     */
    public static String doGet(String url) {
        return doGet(url, CharacterUtil.UTF8, null, DEFAULT_TIMEOUT, DEFAULT_TIMEOUT,
                DEFAULT_TIMEOUT);
    }

    /**
     * 执行get方法
     *
     * @param url
     * @param types
     * @return
     */
    public static String doGet(String url, String[] types) {
        return doGet(url, CharacterUtil.UTF8, types, DEFAULT_TIMEOUT, DEFAULT_TIMEOUT,
                DEFAULT_TIMEOUT);
    }

    /**
     * 执行get方法
     *
     * @param url      请求链接
     * @param encoding 返回编码
     * @return 响应字符串
     */
    public static String doGet(String url, String encoding) {
        return doGet(url, encoding, null, DEFAULT_TIMEOUT, DEFAULT_TIMEOUT,
                DEFAULT_TIMEOUT);
    }

    /**
     * 执行get方法
     *
     * @param url            请求链接
     * @param connectTimeout 连接超时设置
     * @param requestTimeout 请求超时设置
     * @param socketTimeout  socket连接超时设置
     * @return 响应字符串
     */
    public static String doGet(String url, int connectTimeout,
                               int requestTimeout, int socketTimeout) {
        return doGet(url, CharacterUtil.UTF8, null, connectTimeout, requestTimeout,
                socketTimeout);
    }

    /**
     * 执行get方法
     *
     * @param url            请求链接
     * @param connectTimeout 连接超时设置
     * @param requestTimeout 请求超时设置
     * @param socketTimeout  socket连接超时设置
     * @return 响应字符串
     */
    public static String doGet(String url, String encoding, int connectTimeout,
                               int requestTimeout, int socketTimeout) {
        return doGet(url, encoding, null, connectTimeout, requestTimeout,
                socketTimeout);
    }

    /**
     * 计数器
     */
    private final static AtomicInteger counter = new AtomicInteger(0);

    /**
     * 执行get方法
     * <pre>
     *     types = ["image/gif, image/jpeg, image/png"]
     * </pre>
     *
     * @param url
     * @param encoding
     * @param types
     * @param connectTimeout
     * @param requestTimeout
     * @param socketTimeout
     * @return
     */
    public static String doGet(String url, String encoding, String[] types, int connectTimeout,
                               int requestTimeout, int socketTimeout) {
        String result = "";
        try {
            if (log.isDebugEnabled()) {
                log.debug("doGet url=" + url);
                log.debug("doGet connectTimeout=" + connectTimeout + " requestTimeout=" + requestTimeout + " socketTimeout=" + socketTimeout);
            }

            Request request = httpClient.newRequest(url);
            request.method(HttpMethod.GET);
            request.timeout(requestTimeout, TimeUnit.MILLISECONDS);
            ContentResponse response = request.send();
            String mediaType = response.getMediaType();
            boolean isDownload = false;
            if (types != null) {
                for (String type : types) {
                    if (type.equals(mediaType)) {
                        isDownload = true;
                    }
                }
            }

            byte[] bytes = response.getContent();
            if (bytes != null && bytes.length > 0) {
                if (isDownload) {
                    String fileName = System.currentTimeMillis() + String.format("%03d", counter.incrementAndGet());
                    fileName = fileName + "." + mediaType.split("/")[1];
                    fileName = System.getProperty("user.home") + File.separator + fileName;
                    IOUtils.write(bytes, new FileOutputStream(fileName));
                    result = String.format("{ \"file\":\"%s\" }", fileName.replace('\\', '/'));
                } else {
                    result = new String(bytes, encoding);
                }
            }
            if (log.isDebugEnabled()) {
                log.debug("doGet result=" + result);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }

    /**
     * 执行post方法
     *
     * @param url  请求链接
     * @param json 请求json数据
     * @return 响应字符串
     */
    public static String doPost(String url, String json) {
        return doPost(url, json, CharacterUtil.UTF8);
    }

    /**
     * @param url      请求链接
     * @param json     请求json数据
     * @param encoding 请求编码
     * @return 响应字符串
     */
    public static String doPost(String url, String json, String encoding) {
        return doPost(url, json, encoding, DEFAULT_TIMEOUT, DEFAULT_TIMEOUT,
                DEFAULT_TIMEOUT);
    }

    /**
     * @param url            请求链接
     * @param json           请求json数据
     * @param connectTimeout 连接超时设置
     * @param requestTimeout 请求超时设置
     * @param socketTimeout  socket连接超时设置
     * @return 响应字符串
     */
    public static String doPost(String url, String json, int connectTimeout,
                                int requestTimeout, int socketTimeout) {
        return doPost(url, json, CharacterUtil.UTF8, connectTimeout,
                requestTimeout, socketTimeout);
    }

    /**
     * 执行post方法
     *
     * @param url            请求链接
     * @param json           请求json数据
     * @param encoding       请求编码
     * @param connectTimeout 连接超时设置
     * @param requestTimeout 请求超时设置
     * @param socketTimeout  socket连接超时设置
     * @return 响应字符串
     */
    public static String doPost(String url, String json, String encoding,
                                int connectTimeout, int requestTimeout, int socketTimeout) {
        String result = "";
        try {
            if (log.isDebugEnabled()) {
                log.debug("doPost encoding=" + encoding + " url=" + url);
                log.debug("doPost param=" + json);
                log.debug("doPost connectTimeout=" + connectTimeout + " requestTimeout=" + requestTimeout + " socketTimeout=" + socketTimeout);
            }

            Request request = httpClient.newRequest(url);
            request.method(HttpMethod.POST);
            request.timeout(requestTimeout, TimeUnit.MILLISECONDS);
            request.header("Content-Encoding", encoding);
            request.header("Content-Type", "application/json");
            request.header("Content-Length", String.valueOf(json.getBytes(encoding).length));
            request.content(new InputStreamContentProvider(IOUtils.toInputStream(json,
                    encoding)));
            ContentResponse response = request.send();

            byte[] bytes = response.getContent();
            if (bytes != null && bytes.length > 0) {
                result = new String(bytes, encoding);
            }
            if (log.isDebugEnabled()) {
                log.debug("doPost result=" + result);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return result;
    }

    /**
     * 执行post方法
     *
     * @param url 请求链接
     * @param map 请求参数
     * @return 响应字符串
     */
    public static String doPost(String url, Map map) {
        return doPost(url, map, CharacterUtil.UTF8);
    }

    /**
     * 执行post方法
     *
     * @param url      请求链接
     * @param map      请求参数
     * @param encoding 请求编码
     * @return 响应字符串
     */
    public static String doPost(String url, Map map,
                                String encoding) {
        return doPost(url, map, encoding, DEFAULT_TIMEOUT, DEFAULT_TIMEOUT,
                DEFAULT_TIMEOUT);
    }

    /**
     * 执行post方法
     *
     * @param url            请求链接
     * @param map            请求参数
     * @param connectTimeout 连接超时设置
     * @param requestTimeout 请求超时设置
     * @param socketTimeout  socket连接超时设置
     * @return 响应字符串
     */
    public static String doPost(String url, Map map,
                                int connectTimeout, int requestTimeout, int socketTimeout) {
        return doPost(url, map, CharacterUtil.UTF8, connectTimeout,
                requestTimeout, socketTimeout);
    }

    /**
     * 执行post方法
     *
     * @param url            请求链接
     * @param encoding       请求编码
     * @param connectTimeout 连接超时设置
     * @param requestTimeout 请求超时设置
     * @param socketTimeout  socket连接超时设置
     * @return 响应字符串
     */
    public static String doPost(String url, Map map,
                                String encoding, int connectTimeout, int requestTimeout,
                                int socketTimeout) {
        String result = "";
        boolean isUpload = false;
        try {
            if (log.isDebugEnabled()) {
                log.debug("doPost encoding=" + encoding + " url=" + url);
                log.debug("doPost connectTimeout=" + connectTimeout + " requestTimeout=" + requestTimeout + " socketTimeout=" + socketTimeout);
            }

            Fields fields = new Fields();
            Set<Entry> entrySet = map.entrySet();
            for (Entry param : entrySet) {
                Object pk = param.getKey();
                Object pv = param.getValue();
                if (pv instanceof File) {
                    isUpload = true;
                    break;
                }
                fields.add(String.valueOf(pk), String.valueOf(pv));
            }
            ContentProvider provider = new FormContentProvider(fields, Charset.forName(encoding));
            if (isUpload) {
                provider = new MultiPartContentProvider();
                for (Entry param : entrySet) {
                    Object pk = param.getKey();
                    Object pv = param.getValue();
                    if (pv instanceof File) {
                        File file = (File) pv;
                        ((MultiPartContentProvider) provider).addFilePart(String.valueOf(pk), file.getName(), new PathContentProvider(file.toPath()), null);
                    } else {
                        ((MultiPartContentProvider) provider).addFieldPart(String.valueOf(pk), new StringContentProvider(String.valueOf(pv)), null);
                    }
                }
                ((MultiPartContentProvider) provider).close();
            }

            Request request = httpClient.newRequest(url);
            request.method(HttpMethod.POST);
            request.content(provider);
            ContentResponse response = request.send();

            byte[] bytes = response.getContent();
            if (bytes != null && bytes.length > 0) {
                result = new String(bytes, encoding);
            }
            if (log.isDebugEnabled()) {
                log.debug("doPost result=" + result);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return result;
    }

    public static void main(String[] args) {
//        String message = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Resp><head errcode=\"0000\" remoteIp=\"127.0.0.1\" funcId=\"00002\" requestId=\"15777763900350110001\" dataType=\"xml\"/><body><service node=\"http://127.0.0.1:9001\" func=\"10001\"/></body></Resp>";
//        HashMap params = new HashMap();
//        params.put("_message", message);
//        System.out.println(HttpUtil.doPost("http://127.0.0.1:8001/dbs.service", params));
//        System.out.println(0-(-1));
    }
}
