package com.jarveis.frame.util;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketListener;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

/**
 * Websocket客户端
 *
 * @author liuguojun
 * @since 2024-04-01
 */
public final class WsClient implements WebSocketListener {

    private static final Logger log = LoggerFactory.getLogger(WsClient.class);

    private boolean isConnected = false;

    /**
     * 会话对象
     */
    private Session session;
    /**
     * 接收websocket服务器的消息
     */
    private WsListener wsListener;

    /**
     * 构造方法
     * @param wsListener 用于接收websocket服务器返回的消息
     */
    public WsClient(WsListener wsListener) {
        this.wsListener = wsListener;
    }

    /**
     * 连接到websocket服务器
     *
     * @param uri
     */
    public void connect(String uri) {
        try {
            WebSocketClient client = new WebSocketClient();
            client.start();

            URI wsUri = new URI(uri);
            client.connect(this, wsUri);
            waitForConnection();

            if (log.isDebugEnabled()) {
                log.debug("Connecting to :" + wsUri.toString());
            }
        } catch( Exception ex ) {
            log.error(ex.getMessage(), ex);
        }
    }

    public void waitForConnection(){

        if(this.isConnected){
            return; //done
        }

        synchronized(this){
            while(!this.isConnected){
                try {
                    this.wait();
                } catch(InterruptedException ex) {
                    log.error(ex.getMessage(), ex);
                }
            }
        }

    }

    /**
     * 关闭与websocket服务器的连接
     */
    public void close(){
        if (isConnected) {
            this.session.close();
            isConnected = false;
        }
    }

    /**
     * 发消息到websocket服务器
     *
     * @param msg
     */
    public void sendMsg(String msg){
        try {
            if (isConnected) {
                session.getRemote().sendString(msg);
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    @Override
    public void onWebSocketBinary(byte[] bytes, int i, int i1) {

    }

    @Override
    public void onWebSocketText(String s) {
        wsListener.receive(s);
    }

    @Override
    public void onWebSocketClose(int i, String s) {
        this.session = null;
        this.isConnected = false;
    }

    @Override
    public void onWebSocketConnect(Session session) {
        this.session = session;
        this.isConnected = true;
        synchronized (this) {
            notifyAll();
        }
    }

    @Override
    public void onWebSocketError(Throwable th) {
        log.error(th.getMessage(), th);
    }

}
