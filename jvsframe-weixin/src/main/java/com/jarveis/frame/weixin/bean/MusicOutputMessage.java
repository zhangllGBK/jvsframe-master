package com.jarveis.frame.weixin.bean;

/**
 * 音乐输出消息
 * <pre>
 *     <a href="https://developers.weixin.qq.com/doc/offiaccount/Message_Management/Passive_user_reply_message.html">...</a>
 * </pre>
 *
 * @author liuguojun
 * @since 2021-06-01
 */
public class MusicOutputMessage extends OutputMessage {

    private String Title;
    private String Description;
    private String MusicUrl;
    private String HQMusicUrl;
    private String ThumbMediaId;

    public MusicOutputMessage() {
        setMsgType("music");
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getMusicUrl() {
        return MusicUrl;
    }

    public void setMusicUrl(String musicUrl) {
        MusicUrl = musicUrl;
    }

    public String getHQMusicUrl() {
        return HQMusicUrl;
    }

    public void setHQMusicUrl(String HQMusicUrl) {
        this.HQMusicUrl = HQMusicUrl;
    }

    public String getThumbMediaId() {
        return ThumbMediaId;
    }

    public void setThumbMediaId(String thumbMediaId) {
        ThumbMediaId = thumbMediaId;
    }

    public String toWeixin() {
        String str = "<xml>\n" +
                "  <ToUserName><![CDATA[" + getToUserName() + "]]></ToUserName>\n" +
                "  <FromUserName><![CDATA[" + getFromUserName() + "]]></FromUserName>\n" +
                "  <CreateTime>" + System.currentTimeMillis() + "</CreateTime>\n" +
                "  <MsgType><![CDATA[music]]></MsgType>\n" +
                "  <Music>\n" +
                "    <Title><![CDATA[" + getTitle() + "]]></Title>\n" +
                "    <Description><![CDATA[" + getDescription() + "]]></Description>\n" +
                "    <MusicUrl><![CDATA[" + getMusicUrl() + "]]></MusicUrl>\n" +
                "    <HQMusicUrl><![CDATA[" + getHQMusicUrl() + "]]></HQMusicUrl>\n" +
                "    <ThumbMediaId><![CDATA[" + getThumbMediaId() + "]]></ThumbMediaId>\n" +
                "  </Music>\n" +
                "</xml>";
        return str;
    }

    @Override
    public String toCustom() {
        String str = "{" +
                "    \"touser\":\"" + getToUserName() + "\"," +
                "    \"msgtype\":\"music\"," +
                "    \"music\":" +
                "    {" +
                "      \"title\":\"" + getTitle() + "\"," +
                "      \"description\":\"" + getDescription() + "\"," +
                "      \"musicurl\":\"" + getMusicUrl() + "\"," +
                "      \"hqmusicurl\":\"" + getHQMusicUrl() + "\"," +
                "      \"thumb_media_id\":\"" + getThumbMediaId() + "\"" +
                "    }" +
                "}";
        return str;
    }
}
