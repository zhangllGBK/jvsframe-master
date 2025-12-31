package com.jarveis.frame.dbs.jetty;

import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

/**
 * WebSocket处理器
 * 
 * @author liuguojun
 * @since  2023-04-07
 */
public class DbsWebSocketHandler extends WebSocketHandler {

	@Override
	public void configure(WebSocketServletFactory factory) {
		// 设置空闲时间，后关闭连接
		factory.getPolicy().setIdleTimeout(60*1000);

		factory.register(DbsWebSocketListener.class);
	}
}
