package com.jarveis.frame.dbs.jetty;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.jarveis.frame.dbs.DbsUtils;

/**
 * Dbs处理器
 * 
 * @author liuguojun
 * @since  2018-5-24
 */
public class DbsHandler extends AbstractHandler {

	/**
	 * 处理请求
	 *
	 * @param target 请求的目标，URI或名称
	 * @param baseRequest 未被封装的原始请求对象
	 * @param request 被封装的http请求对象
	 * @param response http返回对象
	 * @throws IOException IO异常
	 * @throws ServletException Servlet异常
	 */
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		// 禁止trace请求
		if ("TRACE".equalsIgnoreCase(request.getMethod())) {
			response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			return;
		}

		if (target.endsWith(DbsUtils.SERVICE_SUFFIX)) {
			DbsUtils.dispatchService(request, response);
			baseRequest.setHandled(true);
		} else {
			// DbsUtils.print(response, "please call me.", DbsUtils.HTML_CONTENT_TYPE);
			baseRequest.setHandled(true);
		}
	}

}
