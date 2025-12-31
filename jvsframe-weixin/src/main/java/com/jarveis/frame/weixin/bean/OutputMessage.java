package com.jarveis.frame.weixin.bean;

/**
 * 输出消息
 * <pre>
 *     <a href="https://developers.weixin.qq.com/doc/offiaccount/Message_Management/Passive_user_reply_message.html">被动回复用户消息</a>
 * </pre>
 *
 * @author liuguojun
 * @since 2021-06-01
 */
public class OutputMessage {

    protected String ToUserName;
    protected String FromUserName;
    protected Long CreateTime;
    protected String MsgType;

    /**
     * 获取接收人（用户）
     *
     * @return
     */
    public String getToUserName() {
        return ToUserName;
    }

    /**
     * 设置接收人
     *
     * @param toUserName 接收人（用户）
     */
    public void setToUserName(String toUserName) {
        ToUserName = toUserName;
    }

    /**
     * 获取发送人（公众号）
     *
     * @return
     */
    public String getFromUserName() {
        return FromUserName;
    }

    /**
     * 设置发送人（公众号）
     *
     * @param fromUserName
     */
    public void setFromUserName(String fromUserName) {
        FromUserName = fromUserName;
    }

    /**
     * 获取创建时间
     *
     * @return
     */
    public Long getCreateTime() {
        return CreateTime;
    }

    /**
     * 设置创建时间
     *
     * @param createTime
     */
    public void setCreateTime(Long createTime) {
        CreateTime = createTime;
    }

    /**
     * 返回消息类型
     *
     * @see com.jarveis.frame.weixin.WeixinMsgType
     * @return
     */
    public String getMsgType() {
        return MsgType;
    }

    /**
     * 设置消息类型
     *
     * @see com.jarveis.frame.weixin.WeixinMsgType
     * @param msgType
     */
    public void setMsgType(String msgType) {
        MsgType = msgType;
    }

    /**
     * 转化为微信接收的消息格式
     *
     * @return
     */
    public String toWeixin() {
        return "";
    }

    /**
     *
     *
     * @return
     */
    public String toCustom() {
        return "";
    }
}
