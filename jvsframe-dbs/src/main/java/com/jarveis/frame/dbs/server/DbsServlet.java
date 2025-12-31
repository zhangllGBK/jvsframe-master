package com.jarveis.frame.dbs.server;

import com.jarveis.frame.config.ApplicationConfig;
import com.jarveis.frame.dbs.DbsUtils;
import com.jarveis.frame.util.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 数据访问入口
 * 
 * @author liuguojun
 * @since 2014-06-05
 */
public class DbsServlet extends HttpServlet {

	private static final Logger log = LoggerFactory.getLogger(DbsServlet.class);

	public void init() {
		ApplicationConfig config = new ApplicationConfig();
		config.parse();
		config.execute();
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
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		process(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		process(req, resp);
	}

	/**
	 * 处理
	 * 
	 * @param req 请求对象
	 * @param resp 响应对象
	 */
	private void process(HttpServletRequest req, HttpServletResponse resp) {
		try {
			DbsUtils.dispatchService(req, resp);
		} catch (Exception ex) {
			DbsUtils.print(resp, String.valueOf(Param.ERROR_EXCEPTION),
					DbsUtils.HTML_CONTENT_TYPE);
		}
	}

}
