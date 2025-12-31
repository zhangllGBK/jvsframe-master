package com.jarveis.frame.dbs;

import com.jarveis.frame.bean.ReflectionUtils;
import com.jarveis.frame.util.CharacterUtil;
import com.jarveis.frame.util.Param;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Dbs工具类
 * @author liuguojun
 * @since  2018-08-07
 */
public class DbsUtils {

	private static final Logger log = LoggerFactory.getLogger(DbsUtils.class);

	private static final String SERVICE_ID = "dbs";
	public static final String SERVICE_SUFFIX = ".service";

	private static final String DATA_TYPE = "html,xml,json";

	private static final String[] IP_PROXY = { "X-Forwarded-For",
			"Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP",
			"HTTP_X_FORWARDED_FOR", "X-Real-IP" };

	public static final String HTML_CONTENT_TYPE = "text/html; charset=UTF-8";

	private static final ThreadLocal<HttpServletRequest> reqThreadLocal = new ThreadLocal<HttpServletRequest>();
	private static final ThreadLocal<HttpServletResponse> respThreadLocal = new ThreadLocal<HttpServletResponse>();

	/**
	 * 服务转发
	 * 
	 * @param req 请求对象
	 * @param resp 返回对象
	 */
	public static void dispatchService(HttpServletRequest req, HttpServletResponse resp) {
		try {
			// 设置线程变量
			setHttpRequestAndResponse(req, resp);
			// 封装请求中的输入流
			DbsUtils.packUpload(req);
			// 封装请求参数
			Param in = DbsUtils.packParam(req, resp);
			// 返回数据格式
			String dataType = in.getHead().getString(Param.LABEL_DATATYPE);
			// 设置内部处理的序列化方式
			in.getHead().setProperty(Param.LABEL_DATATYPE, Param.DT_XML);

			if (log.isDebugEnabled()) {
				log.debug("request message = " + in.toXmlString());
			}

			Param out = DbsDispatcher.process(in);
			usedTime(out);

			// xss注入的异常数据，返回空数据
			String funcId = out.getHead().getString(Param.LABEL_FUNCID);
			if (StringUtils.isEmpty(funcId)) {
				dataType = Param.DT_HTML;
			}

			if (Param.DT_HTML.equals(dataType)) {
				out.getHead().setProperty(Param.LABEL_DATATYPE, Param.DT_HTML);
				if (StringUtils.isEmpty(funcId)) {
					// xss注入的异常数据，返回空数据
					DbsUtils.print(resp, StringUtils.EMPTY, HTML_CONTENT_TYPE);
				} else {
					DbsUtils.print(resp, out.getBody().getCDATA(), HTML_CONTENT_TYPE);
				}
			} else if (Param.DT_XML.equals(dataType)) {
				out.getHead().setProperty(Param.LABEL_DATATYPE, Param.DT_XML);
				DbsUtils.print(resp, out.toXmlString(), HTML_CONTENT_TYPE);
			} else {
				out.getHead().setProperty(Param.LABEL_DATATYPE, Param.DT_JSON);
				DbsUtils.print(resp, out.toJsonString(), HTML_CONTENT_TYPE);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			DbsUtils.print(resp, StringUtils.EMPTY, HTML_CONTENT_TYPE);
		} finally {
			DbsCache.getStream();
			DbsCache.getUploads();
			removeHttpRequestAndResponse();
		}
	}

	/**
	 * 封装上传信息
	 * 
	 * @param req 请求对象
	 */
	private static void packUpload(HttpServletRequest req) {
		try {
			if ("false".equalsIgnoreCase(DbsCache.getConst(DbsConst.DBS_FILE_UPLOAD_FLAG))){
				// false:不处理文件上传,其他不做处理
				return;
			}
			if (ServletFileUpload.isMultipartContent(req)) {
				// 默认值 1M
				int upload_memory_size = NumberUtils.toInt(
						DbsCache.getConst("upload_memory_size"), 1048576);
				// 默认值 2M
				int upload_file_size = NumberUtils.toInt(
						DbsCache.getConst("upload_file_size"), 2097152);
				// 默认值 10M
				int upload_request_size = NumberUtils.toInt(
						DbsCache.getConst("upload_request_size"), 10485760);

				// 配置上传参数
				DiskFileItemFactory factory = new DiskFileItemFactory();
				// 设置内存临界值 - 超过后将产生临时文件并存储于临时目录中
				factory.setSizeThreshold(upload_memory_size);
				// 设置临时存储目录
				factory.setRepository(new File(System.getProperty("java.io.tmpdir")));
				ServletFileUpload upload = new ServletFileUpload(factory);
				// 设置最大文件上传值
				upload.setFileSizeMax(upload_file_size);
				// 设置最大请求值 (包含文件和表单数据)
				upload.setSizeMax(upload_request_size);
				// 中文处理
				upload.setHeaderEncoding("UTF-8");

				List<FileItem> formItems = upload.parseRequest(req);
				DbsCache.setUploads(formItems);
			}
		} catch (FileUploadException e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * 封装数据流信息
	 * 
	 * @param req 请求对象
	 */
	private static void packInputStream(HttpServletRequest req) {
		try {
			byte[] bytes = IOUtils.toByteArray(req.getInputStream());
			if (bytes != null && bytes.length > 0) {
				DbsCache.setStream(bytes);
			}
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * 封装请示对象
	 * 
	 * @param req 请求对象
	 * @param resp 返回对象
	 * @return 数据包
	 * @throws Exception 异常
	 */
	private static Param packParam(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		Param in = new Param("Req"); // 请求对象

		/* 封装头部信息 */
		packHead(req, in);
		String funcId = in.getHead().getString(Param.LABEL_FUNCID); // 服务标识

		/* 封装请求参数 */
		if (SERVICE_ID.equals(funcId)) {
			String message = req.getParameter("_message");
			if (StringUtils.isNotEmpty(message)) {
				in = new Param(decrypt(message));
			}
		} else {
			Map<String, String> headmap = in.getHead().getPropertys();
			in = new Param(Param.REQ);
			in.getHead().setPropertys(headmap);
			packBody(req, in);
		}

		in.getHead().setProperty(Param.LABEL_FUNCID, StringUtils.trim(in.getHead().getString(Param.LABEL_FUNCID)));

		/* 客户端请求方法 */
		in.getHead().setProperty(Param.LABEL_METHOD, req.getMethod());

		/* 客户端ip */
		String remoteIp = in.getHead().getString(Param.LABEL_REMOTEIP);
		if (StringUtils.isEmpty(remoteIp)) {
			in.getHead().setProperty(Param.LABEL_REMOTEIP, getRemoteIp(req));
		} else {
			remoteIp = remoteIp.replace(":", ".");
			in.getHead().setProperty(Param.LABEL_REMOTEIP, remoteIp);
		}

		/* 请求流水号 */
		String requestId = in.getHead().getString(Param.LABEL_REQUESTID);
		if (StringUtils.isNotEmpty(requestId)) {
			in.getHead().setProperty(Param.LABEL_PARENTID, requestId);
		}
		in.getHead().setProperty(Param.LABEL_REQUESTID, getRequestId());

		packInputStream(req);

		return in;
	}

	/**
	 * 打包head节点的数据
	 * 
	 * @param req 请求对象
	 */
	private static void packHead(HttpServletRequest req, Param in) {
		String funcId = "00000"; // 服务标识
		String dataType = Param.DT_JSON; // 返回的数据类型
		String appId = null, appVersion = null, device = null, token = null;

		String uri = req.getRequestURI();
		uri = uri.substring(1);
		if (uri.indexOf('?') > -1) {
			uri = uri.substring(0, uri.indexOf('?'));
		}

		if (log.isDebugEnabled()) {
			log.debug("request uri=" + uri);
		}
		String[] arr = uri.split("/");
		for (int i = arr.length - 1; i >= 0; i--) {
			if (i == arr.length - 1) {
				funcId = arr[i];
				funcId = funcId.substring(0, funcId.indexOf('.'));
			} else if (i == arr.length - 2) {
				dataType = arr[i];
				if (!DATA_TYPE.contains(arr[i])) {
					dataType = Param.DT_JSON;
				}
			} else {
				if (NumberUtils.isCreatable(arr[i]) && StringUtils.isEmpty(appVersion)) {
					appVersion = arr[i];
				} else if (arr[i].length() < 32 && StringUtils.isEmpty(appId)) {
					appId = arr[i];
				} else if (arr[i].length() >= 32 && StringUtils.isEmpty(device)) {
					device = arr[i];
				} else if (arr[i].length() >= 32 && StringUtils.isEmpty(token)) {
					token = arr[i];
				}
			}
		}

		in.getHead().setProperty(Param.LABEL_FUNCID, funcId);
		in.getHead().setProperty(Param.LABEL_DATATYPE, dataType);
		if (StringUtils.isNotEmpty(appId)) {
			in.getHead().setProperty("@appId", appId);
		}
		if (StringUtils.isNotEmpty(appVersion)) {
			in.getHead().setProperty("@appVersion", appVersion);
		}
		if (StringUtils.isNotEmpty(device)) {
			in.getHead().setProperty(Param.LABEL_DEVICE, device);
		}
		if (StringUtils.isNotEmpty(token)) {
			in.getHead().setProperty(Param.LABEL_TOKEN, token);
		}
	}

	/**
	 * 打包body节点的数据
	 * 
	 * @param req 返回对象
	 * @param in 返回信息
	 */
	private static void packBody(HttpServletRequest req, Param in) {
		// 设备信息是否存在
		in.getBody().setPropertys(req.getParameterMap());

		// 兼容2.x版本
		String dvalue = in.getBody().getString("@_device");
		String tvalue = in.getBody().getString("@_token");
		if (StringUtils.isNotEmpty(dvalue)) {
			in.getBody().removeProperty("@_device");
			in.getHead().setProperty(Param.LABEL_DEVICE, dvalue);
		}

		if (StringUtils.isNotEmpty(tvalue)) {
			in.getBody().removeProperty("@_token");
			in.getHead().setProperty(Param.LABEL_TOKEN, tvalue);
		}

		// 如果head中不存在，则系统生成
		/*
		if (StringUtils.isEmpty(in.getHead().getString(Param.LABEL_DEVICE))) {
			in.getHead().setProperty(Param.LABEL_DEVICE,
					UUID.randomUUID().toString().replaceAll("-", ""));
		}
		*/
	}

	/**
	 * 输出返回信息
	 * 
	 * @param resp 返回对象
	 * @param message 返回信息
	 */
	public static void print(HttpServletResponse resp, String message, String contentType) {
		PrintWriter writer = null;
		try {
			/* 解决跨域问题 */
			resp.setHeader("Access-Control-Allow-Origin", "*");
			resp.setHeader("Pragma", "No-cache");
			resp.setHeader("Cache-Control", "no-cache");
			resp.setDateHeader("Expires", 0);
			resp.setCharacterEncoding(CharacterUtil.UTF8);
			resp.setContentType(contentType);
			resp.setStatus(HttpServletResponse.SC_OK);
			writer = resp.getWriter();
			writer.print(encrypt(message));
			writer.flush();
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

	/**
	 * 获取请求的客户端的 IP 地址。若应用服务器前端配有反向代理的 Web 服务器， 需要在 Web 服务器中将客户端原始请求的 IP 地址加入到
	 * HTTP header 中。
	 * 
	 * @param request 客户端请求对象
	 * @return 客户端IP
	 */
	public static String getRemoteIp(HttpServletRequest request) {
		String remoteIp = "";
		for (int i = 0; i < IP_PROXY.length; i++) {
			String ip = request.getHeader(IP_PROXY[i]);
			if (ip != null && ip.trim().length() > 0) {
				remoteIp = getRemoteIpFromForward(ip.trim());
				break;
			}
		}
		if (StringUtils.isEmpty(remoteIp)) {
			remoteIp = request.getRemoteHost();
		}
		if (StringUtils.isNotEmpty(remoteIp)) {
			//
			remoteIp = remoteIp.replace(":", ".");
		}
		return remoteIp;
	}

	/**
	 * 从 HTTP Header 中截取客户端连接 IP 地址。如果经过多次反向代理， 在请求头中获得的是以“,”分隔 IP 地址链，第一段为客户端
	 * IP 地址。
	 * 
	 * @param xforwardIp
	 *            从 HTTP 请求头中获取转发过来的 IP 地址链
	 * @return 客户端源 IP
	 */
	private static String getRemoteIpFromForward(String xforwardIp) {
		int offset = xforwardIp.indexOf(',');
		if (offset < 0) {
			return xforwardIp;
		}
		return xforwardIp.substring(0, offset);
	}

	/**
	 * 计数器
	 */
	private final static AtomicInteger counter = new AtomicInteger(0);

	/**
	 * 获取新的请求id
	 * 
	 * @return String
	 */
	public static String getRequestId() {
		String machine = StringUtils.defaultIfEmpty(DbsCache.getConst(DbsConst.DBS_MACHINE), "001");
		if (counter.get() >= 999) {
			counter.set(0);
		}
		return System.currentTimeMillis() + machine
				+ String.format("%03d", counter.incrementAndGet());
	}

	/**
	 * 请求耗时
	 *
	 * @param out 输出数据包
	 */
	private static void usedTime(Param out){
		long currentTime = System.currentTimeMillis();
		String requestId = out.getHead().getString(Param.LABEL_REQUESTID);
		if (requestId.length() > 13) {
			long rquestTime = NumberUtils.toLong(requestId.substring(0, 13));
			out.getHead().setProperty(Param.LABEL_USEDTIME, currentTime - rquestTime);
		}
	}

	/**
	 * 格式化dbs统一访问路径
	 * 
	 * @param uri 访问链接的路径
	 * @return dbs访问路径
	 */
	public static String getDbsURI(String uri) {
		String dbsURI = uri.startsWith("http://") ? uri : "http://" + uri;
		if (!dbsURI.endsWith(SERVICE_ID + SERVICE_SUFFIX)) {
			if (!dbsURI.endsWith("/")) {
				dbsURI += "/";
			}
			dbsURI += SERVICE_ID + SERVICE_SUFFIX;
		}

		return dbsURI;
	}

	/**
	 * 检查funcId是否为系统服务
	 * <pre>
	 *  -1: 无效服务
	 *   0: 系统服务
	 *   1: 用户服务
	 * </pre>
	 *
	 * @param funcId
	 * @return
	 */
	public static int isFuncId(String funcId){
		int number = NumberUtils.toInt(funcId, -1);
		if (number == -1) {
			return number; // 无效服务
		}
		if (number < 10000 || number > 99999) {
			number = 0; // 系统服务
		} else {
			number = 1; // 用户服务
		}

		return number;
	}

	/**
	 * 获取HttpServletRequest
	 * @since 2024-08-05
	 * @return
	 */
	public static HttpServletRequest getHttpRequest() {
		return reqThreadLocal.get();
	}

	/**
	 * 获取HttpServletResponse
	 * @since 2024-08-05
	 * @return
	 */
	public static HttpServletResponse getHttpResponse() {
		return respThreadLocal.get();
	}

	/**
	 * 设置HttpServletRequest, HttpServletResponse
	 * @since 2024-08-19
	 * @param req
	 * @param resp
	 */
	private static void setHttpRequestAndResponse(HttpServletRequest req, HttpServletResponse resp) {
		reqThreadLocal.set(req);
		respThreadLocal.set(resp);
	}

	/**
	 * 移除HttpServletRequest, HttpServletResponse
	 * @since 2024-08-19
	 */
	private static void removeHttpRequestAndResponse() {
		reqThreadLocal.remove();
		respThreadLocal.remove();
	}

	/**
	 * 加密
	 * @since 2024-08-19
	 */
	private static String encrypt(String sourceStr) {
		DbsSecurity security = getSecurity();
		return security != null ? security.encrypt(sourceStr) : sourceStr;
	}

	/**
	 * 解密
	 * @since 2024-08-19
	 */
	private static String decrypt(String encryptStr) {
		DbsSecurity security = getSecurity();
		return security != null ? security.decrypt(encryptStr) : encryptStr;
	}

	/**
	 * 获取安全处理类
	 * @since 2024-08-19
	 * @return
	 */
	private static DbsSecurity getSecurity() {
		String securityClass = DbsCache.getConst(DbsConst.DBS_security_class);
		if (StringUtils.isNotEmpty(securityClass)) {
			Object security = ReflectionUtils.newInstance(securityClass);
			if (security != null && security instanceof DbsSecurity) {
				return (DbsSecurity) security;
			}
		}

		return null;
	}

}
