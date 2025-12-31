package com.jarveis.frame.weixin.bean;

/**
 * success输出消息
 * <pre>
 *     <a href="https://developers.weixin.qq.com/doc/offiaccount/Message_Management/Passive_user_reply_message.html">...</a>
 * </pre>
 *
 * @author liuguojun
 * @since 2021-06-17
 */
public class SuccessOutputMessage extends OutputMessage {

    public String toWeixin() {
        return "success";
    }
}
