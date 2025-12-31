package com.jarveis.frame.weixin.bean;

/**
 * 视频输出消息
 * <pre>
 *     <a href="https://developers.weixin.qq.com/doc/offiaccount/Message_Management/Passive_user_reply_message.html">...</a>
 * </pre>
 *
 * @author liuguojun
 * @since 2021-06-01
 */
public class VideoOutputMessage extends OutputMessage {

    private String MediaId;
    private String Title;
    private String Description;

    public VideoOutputMessage() {
        setMsgType("video");
    }

    public String getMediaId() {
        return MediaId;
    }

    public void setMediaId(String mediaId) {
        MediaId = mediaId;
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

    public String toWeixin() {
        String str = "<xml>\n" +
                "  <ToUserName><![CDATA[" + getToUserName() + "]]></ToUserName>\n" +
                "  <FromUserName><![CDATA[" + getFromUserName() + "]]></FromUserName>\n" +
                "  <CreateTime>" + System.currentTimeMillis() + "</CreateTime>\n" +
                "  <MsgType><![CDATA[video]]></MsgType>\n" +
                "  <Video>\n" +
                "    <MediaId><![CDATA[" + getMediaId() + "]]></MediaId>\n" +
                "    <Title><![CDATA[" + getTitle() + "]]></Title>\n" +
                "    <Description><![CDATA[" + getDescription() + "]]></Description>\n" +
                "  </Video>\n" +
                "</xml>";
        return str;
    }

    @Override
    public String toCustom() {
        String str = "{" +
                "    \"touser\":\"" + getToUserName() + "\"," +
                "    \"msgtype\":\"video\"," +
                "    \"video\":" +
                "    {" +
                "      \"media_id\":\"" + getMediaId() + "\"," +
                "      \"thumb_media_id\":\"\"," +
                "      \"title\":\"" + getTitle() + "\"," +
                "      \"description\":\"" + getDescription() + "\"" +
                "    }" +
                "}";
        return str;
    }
}
