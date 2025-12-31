package com.jarveis.frame.weixin.bean;

/**
 * 文本输出消息
 * <pre>
 *     <a href="https://developers.weixin.qq.com/doc/offiaccount/Message_Management/Passive_user_reply_message.html">...</a>
 * </pre>
 *
 * @author liuguojun
 * @since 2021-06-01
 */
public class TextOutputMessage extends OutputMessage {

    private String Content;

    public TextOutputMessage() {
        setMsgType("text");
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public String toWeixin() {
        String str = "<xml>\n" +
                "  <ToUserName><![CDATA[" + getToUserName() + "]]></ToUserName>\n" +
                "  <FromUserName><![CDATA[" + getFromUserName() + "]]></FromUserName>\n" +
                "  <CreateTime>" + System.currentTimeMillis() + "</CreateTime>\n" +
                "  <MsgType><![CDATA[text]]></MsgType>\n" +
                "  <Content><![CDATA[" + getContent() + "]]></Content>\n" +
                "</xml>";
        return str;
    }

    public String toCustom() {
        String str = "{" +
                "    \"touser\":\"" + getToUserName() + "\"," +
                "    \"msgtype\":\"text\"," +
                "    \"text\":" +
                "    {" +
                "         \"content\":\"" + getContent() + "\"" +
                "    }" +
                "}";
        return str;
    }
}
