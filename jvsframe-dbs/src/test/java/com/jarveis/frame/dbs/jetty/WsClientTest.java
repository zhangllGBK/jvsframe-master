package com.jarveis.frame.dbs.jetty;


import com.jarveis.frame.util.WsClient;
import com.jarveis.frame.util.WsListener;

public class WsClientTest {

    public static void main(String[] args) throws Exception {
        WsClient wsClient = new WsClient(new WsListener() {
            @Override
            public void receive(String msg) {
                //System.out.println(msg);
            }
        });
        wsClient.connect("ws://127.0.0.1:9000/ws");

        String message = "{";
        message += "'head':{'appId':'8888','appVersion':'4.0','device':'a7bf1feda8124fd7a15b302691ba164f','token':'dbded8a69c9a41d4908a24f5c58ae419','funcId':'10001','dataType':'json'},";
        message += "'body':{'name':'Tom'}";
        message += "}";
//        Param param = new Param(message);

        long now = System.currentTimeMillis();
        for(int i =0; i < 10000; i++) {
//            param.getBody().setProperty("@name", Math.random());
//            wsClient.sendMsg(param.toJsonString());
            wsClient.sendMsg(message);
        }
        System.out.println("time=" + (System.currentTimeMillis() - now));

        wsClient.close();
    }
}
