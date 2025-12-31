package com.jarveis.frame.weixin.bean;

/**
 * 图片输出消息
 * <pre>
 *     <a href="https://developers.weixin.qq.com/doc/offiaccount/Message_Management/Passive_user_reply_message.html">...</a>
 * </pre>
 *
 * @author liuguojun
 * @since 2021-06-03
 */
public class ImageOutputMessage extends OutputMessage {

    private String MediaId;

    public ImageOutputMessage(){
        setMsgType("image");
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
                "  <CreateTime>"+ System.currentTimeMillis() +"</CreateTime>\n" +
                "  <MsgType><![CDATA[image]]></MsgType>\n" +
                "  <Image>\n" +
                "    <MediaId><![CDATA[" + getMediaId() + "]]></MediaId>\n" +
                "  </Image>\n" +
                "</xml>";
        return str;
    }

    @Override
    public String toCustom() {
        String str = "{" +
                "    \"touser\":\"" + getToUserName() + "\"," +
                "    \"msgtype\":\"image\"," +
                "    \"image\":" +
                "    {" +
                "         \"media_id\":\"" + getMediaId() + "\"" +
                "    }" +
                "}";
        return str;
    }
}
