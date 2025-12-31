package com.jarveis.frame.weixin.bean;

/**
 * 小程序新闻输出消息
 * <pre>
 *     <a href="https://developers.weixin.qq.com/doc/offiaccount/Message_Management/Service_Center_messages.html#">...</a>客服接口-发消息
 * </pre>
 *
 * @author liuguojun
 * @since 2021-10-20
 */
public class MpNewsOutputMessage extends OutputMessage {

    private String articleId;

    public MpNewsOutputMessage() {
        setMsgType("mpnewsarticle");
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public String getArticleId() {
        return articleId;
    }

    public String toWeixin() {
        String str = "";
        return str;
    }

    public String toCustom() {
        String str = "{" +
                "    \"touser\":\"" + getToUserName() + "\"," +
                "    \"msgtype\":\"mpnewsarticle\"," +
                "    \"mpnewsarticle\": {" +
                "         \"article_id\":\"" + getArticleId() + "\"" +
                "    }" +
                "}";
        return str;
    }

}
