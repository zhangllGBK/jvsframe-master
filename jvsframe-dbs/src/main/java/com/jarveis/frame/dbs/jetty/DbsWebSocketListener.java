package com.jarveis.frame.dbs.jetty;

import com.jarveis.frame.dbs.DbsDispatcher;
import com.jarveis.frame.util.Param;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * websocket监听器的实现类
 *
 * @author liuguojun
 * @since 2023-04-07
 */
public class DbsWebSocketListener implements WebSocketListener {

    private static final Logger log = LoggerFactory.getLogger(DbsWebSocketListener.class);

    private Session session;
    private RemoteEndpoint remote;

    public Session getSession() {
        return session;
    }

    public RemoteEndpoint getRemote() {
        return remote;
    }

    public boolean isConnected() {
        return (getSession() != null) && (getSession().isOpen());
    }

    @Override
    public void onWebSocketBinary(byte[] payload, int offset, int len) {

    }

    @Override
    public void onWebSocketText(String message) {
        try {
            if (isConnected()) {
                Param in = new Param(message);
                Param out = DbsDispatcher.process(in);
                getRemote().sendString(out.toJsonString());
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    @Override
    public void onWebSocketConnect(Session session) {
        log.info("WebSocket is Connect. remoteAddress=" + session.getRemoteAddress());
        this.session = session;
        this.remote = session.getRemote();

    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        log.info("WebSocket is Close. statusCode=" + statusCode + ", reason=" + reason);
        this.remote = null;
        this.session = null;
    }

    @Override
    public void onWebSocketError(Throwable th) {
        log.error(th.getMessage(), th);
    }
}
