package com.jarveis.frame.weixin.dbs;

import com.alibaba.fastjson.JSONObject;
import com.jarveis.frame.dbs.Service;
import com.jarveis.frame.util.HttpUtil;
import com.jarveis.frame.util.JsonUtil;
import com.jarveis.frame.util.Param;
import com.jarveis.frame.weixin.WeixinAccessToken;
import com.jarveis.frame.weixin.WeixinCallback;
import com.jarveis.frame.weixin.WeixinURL;
import com.jarveis.frame.weixin.WeixinUtils;
import com.jarveis.frame.weixin.bean.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 创建客服账户
 * <pre>
 *     接口文档：
 *     <a href="https://developers.weixin.qq.com/doc/offiaccount/Message_Management/Service_Center_messages.html#">...</a>客服接口-发消息
 *
 *     输出：
 *
 * </pre>
 *
 * @author liuguojun
 * @see WeixinURL#SEND_CUSTOM_MESSAGE
 * @since 2021-10-19
 */
public class KfMessageSendService implements Service, WeixinCallback, WeixinAccessToken {

    private static final Logger log = LoggerFactory.getLogger(KfMessageSendService.class);

    @Override
    public Param callService(Param in) {
        Param out = null;
        try {
            out = new Param(Param.RESP);
            String touser = in.getBody().getString("@touser"); // 微信用户openid
            String msgtype = in.getBody().getString("@msgtype"); // 消息类型
            if (StringUtils.isAnyEmpty(touser, msgtype)) {
                out.getHead().setProperty(Param.LABEL_ERROR, Param.ERROR_EXCEPTION);
                return out;
            }

            // 获取accessToken
            String accessToken = getAccessToken(in);

            Param child = in.getBody().getChild("");
            String postJson = StringUtils.EMPTY;
            switch (msgtype) {
                case "text":
                    postJson = getTextJson(child, touser);
                    break;
                case "image":
                    postJson = getImageJson(child, touser);
                    break;
                case "voice":
                    postJson = getVoiceJson(child, touser);
                    break;
                case "video":
                    postJson = getVideoJson(child, touser);
                    break;
                case "music":
                    postJson = getMusicJson(child, touser);
                    break;
                case "news":
                    postJson = getNewsJson(child, touser);
                    break;
                case "mpnews":
                    postJson = getMpnewsJson(child, touser);
                    break;
                case "msgmenu":
                    postJson = getMsgmenuJson(child, touser);
                    break;
                case "wxcard":
                    postJson = getWxcardJson(child, touser);
                    break;
                default:
                    break;
            }

            // 请求接口
            String postURL = String.format(WeixinURL.SEND_CUSTOM_MESSAGE, accessToken);
            // 请求参数
            String result = HttpUtil.doPost(postURL, postJson);
            if (StringUtils.isEmpty(result)) {
                out.getHead().setProperty(Param.LABEL_ERROR, Param.ERROR_EXCEPTION);
                return out;
            }

            // 解析返回数据
            JSONObject jsonObject = (JSONObject) JsonUtil.parse(result);
            // 封装返回数据包
            WeixinUtils.packOutParam(jsonObject, out);
            callback(in, out);
        } catch (Exception ex) {
            if (out != null) {
                out.getHead().setProperty(Param.LABEL_ERROR, Param.ERROR_EXCEPTION);
                out.getBody().addCDATA("error");
            }
            log.error(ex.getMessage(), ex);
        }
        return out;
    }

    /**
     * 客服发送文本消息
     *
     * @param child
     * @param touser
     * @return
     */
    private String getTextJson(Param child, String touser) {
        TextOutputMessage om = new TextOutputMessage();
        om.setToUserName(touser);
        om.setContent(child.getString("@content"));
        return om.toCustom();
    }

    /**
     * 客服发送图片消息
     *
     * @param child
     * @param touser
     * @return
     */
    private String getImageJson(Param child, String touser) {
        ImageOutputMessage om = new ImageOutputMessage();
        om.setToUserName(touser);
        om.setMediaId(child.getString("@media_id"));
        return om.toCustom();
    }

    /**
     * 客服发送语音消息
     *
     * @param child
     * @param touser
     * @return
     */
    private String getVoiceJson(Param child, String touser) {
        VoiceOutputMessage om = new VoiceOutputMessage();
        om.setToUserName(touser);
        om.setMediaId(child.getString("@media_id"));
        return om.toCustom();
    }

    /**
     * 客服发送视频消息
     *
     * @param child
     * @param touser
     * @return
     */
    private String getVideoJson(Param child, String touser) {
        VideoOutputMessage om = new VideoOutputMessage();
        om.setToUserName(touser);
        om.setMediaId(child.getString("@media_id"));
        om.setTitle(child.getString("@title"));
        om.setDescription(child.getString("@description"));
        return om.toCustom();
    }

    /**
     * 客服发送唱片消息
     *
     * @param child
     * @param touser
     * @return
     */
    private String getMusicJson(Param child, String touser) {
        MusicOutputMessage om = new MusicOutputMessage();
        om.setToUserName(touser);
        om.setTitle(child.getString("@title"));
        om.setDescription(child.getString("@description"));
        om.setMusicUrl(child.getString("@musicurl"));
        om.setHQMusicUrl(child.getString("@hqmusicurl"));
        om.setThumbMediaId(child.getString("@media_id"));
        return om.toCustom();
    }

    /**
     * 客服发送新闻消息
     *
     * @param child
     * @param touser
     * @return
     */
    private String getNewsJson(Param child, String touser) {
        return StringUtils.EMPTY;
    }

    /**
     * 客服发送小程序新闻消息
     *
     * @param child
     * @param touser
     * @return
     */
    private String getMpnewsJson(Param child, String touser) {
        return StringUtils.EMPTY;
    }

    /**
     * 客服发送菜单消息
     *
     * @param child
     * @param touser
     * @return
     */
    private String getMsgmenuJson(Param child, String touser) {
        return StringUtils.EMPTY;
    }

    /**
     * 客服发送卡券消息
     *
     * @param child
     * @param touser
     * @return
     */
    private String getWxcardJson(Param child, String touser) {
        return StringUtils.EMPTY;
    }

    @Override
    public void callback(Param in, Param out) {
    }

    @Override
    public String getAccessToken(Param in) {
        return WeixinUtils.getAccessToken(false);
    }
}
