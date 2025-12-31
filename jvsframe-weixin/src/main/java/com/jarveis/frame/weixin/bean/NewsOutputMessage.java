package com.jarveis.frame.weixin.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 图文输出消息
 * <pre>
 *     <a href="https://developers.weixin.qq.com/doc/offiaccount/Message_Management/Passive_user_reply_message.html">...</a>
 * </pre>
 *
 * @author liuguojun
 * @since 2021-06-03
 */
public class NewsOutputMessage extends OutputMessage {

    private List<Article> articles;

    public NewsOutputMessage() {
        setMsgType("news");
        this.articles = new ArrayList<Article>();
    }

    public void addArticle(Article article) {
        this.articles.add(article);
    }

    public String toWeixin() {
        String str = "<xml>\n" +
                "  <ToUserName><![CDATA[" + getToUserName() + "]]></ToUserName>\n" +
                "  <FromUserName><![CDATA[" + getFromUserName() + "]]></FromUserName>\n" +
                "  <CreateTime>" + System.currentTimeMillis() + "</CreateTime>\n" +
                "  <MsgType><![CDATA[news]]></MsgType>\n";
        if (!articles.isEmpty()) {
            str += "  <ArticleCount>" + articles.size() + "</ArticleCount>\n";
            for (Article article : articles) {
                str += "  <Articles>\n" +
                        "    <item>\n" +
                        "      <Title><![CDATA[" + article.getTitle() + "]]></Title>\n" +
                        "      <Description><![CDATA[" + article.getDescription() + "]]></Description>\n" +
                        "      <PicUrl><![CDATA[" + article.getPicUrl() + "]]></PicUrl>\n" +
                        "      <Url><![CDATA[" + article.getUrl() + "]]></Url>\n" +
                        "    </item>\n" +
                        "  </Articles>\n";
            }
        }
        str += "</xml>";
        return str;
    }

    @Override
    public String toCustom() {
        String str = "";
        return str;
    }

    class Article {
        private String Title;
        private String Description;
        private String PicUrl;
        private String Url;

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

        public String getPicUrl() {
            return PicUrl;
        }

        public void setPicUrl(String picUrl) {
            PicUrl = picUrl;
        }

        public String getUrl() {
            return Url;
        }

        public void setUrl(String url) {
            Url = url;
        }
    }
}
