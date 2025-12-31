package com.jarveis.frame.util;

/**
 * WebSocket监听器
 *
 * @author liuguojun
 * @since 2024-04-02
 */
public interface WsListener {

    void receive(String msg);

}
