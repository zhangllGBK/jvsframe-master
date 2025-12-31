package com.jarveis.frame.weixin;

import com.jarveis.frame.weixin.bean.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 微信消息路由规则
 *
 * @author liuguojun
 * @since 2021-06-07
 */
public class WeixinRouterRule {

    private static final Logger log = LoggerFactory.getLogger(WeixinRouterRule.class);

    private WeixinRouter weixinRouter;
    private String msgType;
    private String content;
    private String event;
    private String eventKey;
    private WeixinHandler weixinHandler;

    /**
     * 路由规则构造器
     *
     * @param weixinRouter 路由器
     */
    protected WeixinRouterRule(WeixinRouter weixinRouter) {
        this.weixinRouter = weixinRouter;
    }

    /**
     * 设置消息类型
     *
     * @see com.jarveis.frame.weixin.WeixinMsgType
     * @param msgType 事件类型
     * @return WeixinRouterRule
     */
    public WeixinRouterRule setMsgType(String msgType) {
        this.msgType = msgType;
        return this;
    }

    /**
     * 设置消息类型
     *
     * @param msgType 事件类型
     * @return WeixinRouterRule
     */
    public WeixinRouterRule setMsgType(WeixinMsgType msgType) {
        return setMsgType(msgType.toString());
    }

    /**
     * 设置要匹配的内容
     *
     * @param content 匹配的内容
     * @return WeixinRouterRule
     */
    public WeixinRouterRule setContent(String content) {
        this.content = content;
        return this;
    }

    /**
     * 设置匹配的事件名称
     *
     * @see com.jarveis.frame.weixin.WeixinEvent
     * @param event 事件名
     * @return WeixinRouterRule
     */
    public WeixinRouterRule setEvent(String event) {
        this.event = event;
        return this;
    }

    /**
     * 设置匹配的事件名称
     *
     * @param event 事件名
     * @return WeixinRouterRule
     */
    public WeixinRouterRule setEvent(WeixinEvent event) {
        return setEvent(event.toString());
    }

    /**
     * 设置匹配的事件值
     *
     * @param eventKey 事件值
     * @return WeixinRouterRule
     */
    public WeixinRouterRule setEventKey(String eventKey) {
        this.eventKey = eventKey;
        return this;
    }

    /**
     * 设置规则处理器
     *
     * @param weixinHandler 规则处理器
     * @return WeixinRouterRule
     */
    public WeixinRouterRule setWeixinHandler(WeixinHandler weixinHandler) {
        this.weixinHandler = weixinHandler;
        return this;
    }

    /**
     * 将规则添加到路由器
     */
    public void end() {
        if (log.isDebugEnabled()) {
            log.debug(String.format("rule[msgType=%s, content=%s, event=%s, eventKey=%s]",
                    msgType == null ? "" : msgType,
                    content == null ? "" : content,
                    event == null ? "" : event,
                    eventKey == null ? "" : eventKey));
        }
        this.weixinRouter.addRule(this);
    }

    /**
     * 测试路由规则
     *
     * @param inputMessage 输入消息
     * @return boolean
     */
    public boolean test(InputMessage inputMessage) {
        if (msgType != null && msgType.equalsIgnoreCase(inputMessage.getMsgType())) {
            if (inputMessage instanceof TextInputMessage) {

                // 文本消息
                TextInputMessage textInputMessage = (TextInputMessage) inputMessage;
                return content == null || content.equalsIgnoreCase(textInputMessage.getContent());

            } else if (inputMessage instanceof EventInputMessage) {

                // 事件消息
                EventInputMessage eventInputMessage = (EventInputMessage) inputMessage;
                return event == null || event.equalsIgnoreCase(eventInputMessage.getEvent());

            } else if (inputMessage instanceof ImageInputMessage) {

                // 图片消息
                return  true;

            } else if (inputMessage instanceof VoiceInputMessage) {

                // 语音消息
                return true;

            } else if (inputMessage instanceof LinkInputMessage) {

                // 链接消息
                return true;

            } else if (inputMessage instanceof VideoInputMessage) {

                // 视频消息
                return true;

            }
        }

        return false;
    }

    /**
     * 消息处理
     *
     * @param inputMessage 输入消息
     * @return OutputMessage
     */
    protected OutputMessage execute(InputMessage inputMessage) {
        if (weixinHandler != null) {
            return weixinHandler.execute(inputMessage);
        } else {
            return null;
        }
    }


    public static void main(String[] args) {
        TextInputMessage inputMessage = new TextInputMessage();
        inputMessage.setMsgType("text");
        inputMessage.setContent("hello");

        WeixinRouterRule rule = new WeixinRouterRule(null);
        rule.setMsgType("text");
        System.out.println(rule.test(inputMessage));
    }
}
