package com.jarveis.frame.weixin.bean;

/**
 * 客服消息
 * <pre>
 *     <a href="https://developers.weixin.qq.com/doc/offiaccount/Customer_Service/Forwarding_of_messages_to_service_center.html">...</a>
 * </pre>
 *
 * @author liuguojun
 * @since 2021-06-04
 */
public class KefuOutputMessage extends OutputMessage {

    public KefuOutputMessage() {
        setMsgType("transfer_customer_service");
    }

    public String toWeixin() {
        String str = "<xml> \n" +
                "  <ToUserName><![CDATA[" + getToUserName() + "]]></ToUserName>\n" +
                "  <FromUserName><![CDATA[" + getFromUserName() + "]]></FromUserName>\n" +
                "  <CreateTime>" + System.currentTimeMillis() + "</CreateTime>\n" +
                "  <MsgType><![CDATA[transfer_customer_service]]></MsgType>\n" +
                "</xml>";
        return str;
    }

}
