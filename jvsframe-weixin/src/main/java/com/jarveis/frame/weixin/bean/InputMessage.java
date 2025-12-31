package com.jarveis.frame.weixin.bean;

import java.io.Serializable;

/**
 * 输入消息
 * <pre>
 *     <a href="https://developers.weixin.qq.com/doc/offiaccount/Message_Management/Receiving_standard_messages.html">接收普通消息</a>
 * </pre>
 *
 * @author liuguojun
 * @since 2021-06-03
 */
public class InputMessage implements Serializable {

    protected String ToUserName;
    protected String FromUserName;
    protected String CreateTime;
    protected String MsgType = "text";
    protected String MsgId;
    /**
     * 微信公众号/服务号字段
     */
    protected String MsgDataId;
    /**
     * 微信公众号/服务号字段
     */
    protected String Idx;
    /**
     * 企业微信字段
     */
    protected String AgentID;

    /**
     * 获取接收人（公众号）
     *
     * @return String
     */
    public String getToUserName() {
        return ToUserName;
    }

    /**
     * 设置接收人（公众号）
     *
     * @param toUserName 接收人
     */
    public void setToUserName(String toUserName) {
        ToUserName = toUserName;
    }

    /**
     * 获取发送人（用户）
     *
     * @return String
     */
    public String getFromUserName() {
        return FromUserName;
    }

    /**
     * 设置发送人（用户）
     *
     * @param fromUserName 发送人
     */
    public void setFromUserName(String fromUserName) {
        FromUserName = fromUserName;
    }

    /**
     * 获取创建时间
     *
     * @return String
     */
    public String getCreateTime() {
        return CreateTime;
    }

    /**
     * 设置创建时间
     *
     * @param createTime 创建时间
     */
    public void setCreateTime(String createTime) {
        CreateTime = createTime;
    }

    /**
     * 获取消息类型
     *
     * @see com.jarveis.frame.weixin.WeixinMsgType
     * @return String
     */
    public String getMsgType() {
        return MsgType;
    }

    /**
     * 设置消息类型
     *
     * @see com.jarveis.frame.weixin.WeixinMsgType
     * @param msgType 消息类型
     */
    public void setMsgType(String msgType) {
        MsgType = msgType;
    }

    /**
     * 获取消息Id
     *
     * @return String
     */
    public String getMsgId() {
        return MsgId;
    }

    /**
     * 设置消息Id
     *
     * @param msgId 消息Id
     */
    public void setMsgId(String msgId) {
        MsgId = msgId;
    }

    /**
     * 获取消息的数据ID
     *
     * @since 2022-05-18
     * @return String
     */
    public String getMsgDataId() {
        return MsgDataId;
    }

    /**
     * 设置消息的数据ID
     *
     * @since 2022-05-18
     * @param msgDataId
     */
    public void setMsgDataId(String msgDataId) {
        MsgDataId = msgDataId;
    }

    /**
     * 获取消息来源的索引，多图文时第几篇文章，从1开始
     *
     * @since 2022-05-18
     * @return
     */
    public String getIdx() {
        return Idx;
    }

    /**
     * 设置消息来源的索引，多图文时第几篇文章，从1开始
     *
     * @since 2022-05-18
     * @param idx
     */
    public void setIdx(String idx) {
        Idx = idx;
    }

    /**
     * 获取企业应用的id
     *
     * @return
     */
    public String getAgentID() {
        return AgentID;
    }

    /**
     * 设置企业应用的id
     * @param agentID
     */
    public void setAgentID(String agentID) {
        AgentID = agentID;
    }
}
