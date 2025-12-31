package com.jarveis.frame.weixin.bean;

/**
 * 链接输入消息
 *
 * @author liuguojun
 * @since 2021-06-03
 */
public class LinkInputMessage extends InputMessage {

    private String Title;
    private String Description;
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

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }
}
