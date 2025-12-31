package com.jarveis.frame.weixin.bean;

/**
 * 文本输入消息
 * <pre>
 *     <a href="https://developers.weixin.qq.com/doc/offiaccount/Message_Management/Service_Center_messages.html#">...</a>客服接口-发消息
 *     Bizmsgmenuid 参考菜单消息，用户的响应
 * </pre>
 *
 * @author liuguojun
 * @since 2021-06-01
 */
public class TextInputMessage extends InputMessage {

    private String Content;
    private String Bizmsgmenuid;

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public String getBizmsgmenuid() {
        return Bizmsgmenuid;
    }

    public void setBizmsgmenuid(String bizmsgmenuid) {
        Bizmsgmenuid = bizmsgmenuid;
    }
}
