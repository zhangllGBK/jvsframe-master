package com.jarveis.frame.dbs.jetty;

import com.jarveis.frame.util.HttpUtil;

import java.util.HashMap;

public class HttpUtilTest {
    public static void main(String[] args) throws Exception {
        String result;
        HashMap params = new HashMap();

        String message = "{";
        message += "'head':{'appId':'8888','appVersion':'4.0','device':'a7bf1feda8124fd7a15b302691ba164f','token':'dbded8a69c9a41d4908a24f5c58ae419','funcId':'10001','dataType':'json'},";
        message += "'body':{'name':'Tom'}";
        message += "}";
//        Param param = new Param(message);

        long now = System.currentTimeMillis();
        for(int i =0; i < 10000; i++) {
//            param.getBody().setProperty("@name", Math.random());
//            params.put("_message", param.toJsonString());
            params.put("_message", message);
            HttpUtil.doPost("http://127.0.0.1:9000/dbs.service", params);
        }
        System.out.println("time=" + (System.currentTimeMillis() - now));


    }
}
