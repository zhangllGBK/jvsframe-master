package com.jarveis.frame.weixin;

/**
 * 微信输入消息类型
 *
 * @author liuguojun
 * @since 2021-06-08
 */
public enum WeixinMsgType {
    /**
     * 文本消息
     */
    TEXT,

    /**
     * 图片消息
     */
    IMAGE,

    /**
     * 语音消息
     */
    VOICE,

    /**
     * 视频消息:
     */
    VIDEO,

    /**
     * 短视频消息
     */
    SHORTVIDEO,

    /**
     * 位置消息
     */
    LOCATION,

    /**
     * 链接消息
     */
    LINK,

    /**
     * 事件消息
     */
    EVENT,

    /**
     * 图文消息
     */
    NEWS,

    /**
     * 图文消息
     */
    MPNEWS,

    /**
     * 卡券
     */
    WXCARD,

    /**
     * 小程序
     */
    MINIPROGRAMPAGE,

    /**
     * 将消息转发到客服
     * <pre>
     *     <a href="https://developers.weixin.qq.com/doc/offiaccount/Customer_Service/Forwarding_of_messages_to_service_center.html">将消息转发到客服</a>
     * </pre>
     */
    TRANSFER_CUSTOMER_SERVICE,

    /**
     * 菜单消息
     * <pre>
     *     <a href="https://developers.weixin.qq.com/doc/offiaccount/Message_Management/Service_Center_messages.html#">菜单消息</a>
     * </pre>
     */
    msgmenu
}
