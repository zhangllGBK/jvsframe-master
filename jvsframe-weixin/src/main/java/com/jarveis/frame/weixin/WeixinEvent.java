package com.jarveis.frame.weixin;

/**
 * 微信事件类型
 *
 * @author liuguojun
 * @since 2021-06-08
 */
public enum WeixinEvent {
    /**
     * 关注
     * <pre>
     *     <a href="https://developers.weixin.qq.com/doc/offiaccount/Message_Management/Receiving_event_pushes.html"></a>
     * </pre>
     */
    SUBSCRIBE,

    /**
     * 取消关注
     */
    UNSUBSCRIBE,

    /**
     * 扫码
     */
    SCAN,

    /**
     * 扫码推事件的事件推送
     * <pre>
     *     <a href="https://developers.weixin.qq.com/doc/offiaccount/Custom_Menus/Custom_Menu_Push_Events.html#2">scancode_push</a>
     * </pre>
     */
    SCANCODE_PUSH,

    /**
     * 扫码推事件且弹出“消息接收中”提示框的事件推送
     * <pre>
     *     <a href="https://developers.weixin.qq.com/doc/offiaccount/Custom_Menus/Custom_Menu_Push_Events.html#3">scancode_waitmsg</a>
     * </pre>
     */
    SCANCODE_WAITMSG,

    /**
     * 弹出系统拍照发图的事件推送
     * <pre>
     *     <a href="https://developers.weixin.qq.com/doc/offiaccount/Custom_Menus/Custom_Menu_Push_Events.html#4">pic_sysphoto</a>
     * </pre>
     */
    PIC_SYSPHOTO,

    /**
     * 弹出拍照或者相册发图的事件推送
     * <pre>
     *     <a href="https://developers.weixin.qq.com/doc/offiaccount/Custom_Menus/Custom_Menu_Push_Events.html#5">pic_photo_or_album</a>
     * </pre>
     */
    PIC_PHOTO_OR_ALBUM,

    /**
     * 弹出微信相册发图器的事件推送
     * <pre>
     *     <a href="https://developers.weixin.qq.com/doc/offiaccount/Custom_Menus/Custom_Menu_Push_Events.html#6">pic_weixin</a>
     * </pre>
     */
    PIC_WEIXIN,

    /**
     * 位置
     */
    LOCATION,

    /**
     * 弹出地理位置选择器的事件推送
     * <pre>
     *     <a href="https://developers.weixin.qq.com/doc/offiaccount/Custom_Menus/Custom_Menu_Push_Events.html#7">location_select</a>
     * </pre>
     */
    LOCATION_SELECT,

    /**
     * 点击菜单拉取消息时的事件推送
     * <pre>
     *     <a href="https://developers.weixin.qq.com/doc/offiaccount/Custom_Menus/Custom_Menu_Push_Events.html#0">click</a>
     * </pre>
     */
    CLICK,

    /**
     * 点击菜单跳转链接时的事件推送
     * <pre>
     *     <a href="https://developers.weixin.qq.com/doc/offiaccount/Custom_Menus/Custom_Menu_Push_Events.html#1">view</a>
     * </pre>
     */
    VIEW,

    /**
     * 点击菜单跳转小程序的事件推送
     * <pre>
     *     <a href="https://developers.weixin.qq.com/doc/offiaccount/Custom_Menus/Custom_Menu_Push_Events.html#8">view_miniprogram</a>
     * </pre>
     */
    view_miniprogram,

    /**
     * 模板发送成功响应
     * <pre>
     *     <a href="https://developers.weixin.qq.com/doc/offiaccount/Message_Management/Template_Message_Interface.html">TEMPLATESENDJOBFINISH</a>
     * </pre>
     */
    TEMPLATESENDJOBFINISH,

    /**
     * 事件推送发布结果
     * <pre>
     *     <a href="https://developers.weixin.qq.com/doc/offiaccount/Publish/Callback_on_finish.html">PUBLISHJOBFINISH</a>
     * </pre>
     */
    PUBLISHJOBFINISH,

    /**
     * 授权用户资料变更
     * <pre>
     *     <a href="https://developers.weixin.qq.com/doc/offiaccount/OA_Web_Apps/authorization_change.html">user_info_modified</a>
     * </pre>
     */
    USER_INFO_MODIFIED,

    /**
     * 微信用户扫顾问二维码后会触发事件推送
     * <pre>
     *     <a href="https://developers.weixin.qq.com/doc/offiaccount/Shopping_Guide/guide-account/shopping-guide.onGuideCreateQrCode.html">guide_qrcode_scan_event</a>
     * </pre>
     */
    GUIDE_QRCODE_SCAN_EVENT
}
