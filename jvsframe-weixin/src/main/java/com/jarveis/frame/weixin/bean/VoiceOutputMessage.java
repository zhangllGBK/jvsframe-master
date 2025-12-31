package com.jarveis.frame.weixin.bean;

/**
 * 语音输出消息
 * <pre>
 *     <a href="https://developers.weixin.qq.com/doc/offiaccount/Message_Management/Passive_user_reply_message.html">...</a>
 * </pre>
 *
 * @author liuguojun
 * @since 2021-06-01
 */
public class VoiceOutputMessage extends OutputMessage {

    private String MediaId;

    public VoiceOutputMessage(){
        setMsgType("voice");
    }

    public String getMediaId() {
        return MediaId;
    }

    public void setMediaId(String mediaId) {
        MediaId = mediaId;
    }

    public String toWeixin() {
        String str = "<xml>\n" +
                "  <ToUserName><![CDATA[" + getToUserName() + "]]></ToUserName>\n" +
                "  <FromUserName><![CDATA[" + getFromUserName() + "]]></FromUserName>\n" +
                "  <CreateTime>" + System.currentTimeMillis() + "</CreateTime>\n" +
                "  <MsgType><![CDATA[voice]]></MsgType>\n" +
                "  <Voice>\n" +
                "    <MediaId><![CDATA[" + getMediaId() + "]]></MediaId>\n" +
                "  </Voice>\n" +
                "</xml>";
        return str;
    }

    @Override
    public String toCustom() {
        String str = "{" +
                "    \"touser\":\"" + getToUserName() + "\"," +
                "    \"msgtype\":\"voice\"," +
                "    \"image\":" +
                "    {" +
                "         \"media_id\":\"" + getMediaId() + "\"" +
                "    }" +
                "}";
        return str;
    }

}
