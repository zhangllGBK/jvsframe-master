package com.jarveis.frame.weixin.bean;

/**
 * 图片输入消息
 *
 * @author liuguojun
 * @since 2021-06-03
 */
public class ImageInputMessage extends InputMessage{

    private String PicUrl;
    private String MediaId;

    public String getPicUrl() {
        return PicUrl;
    }

    public void setPicUrl(String picUrl) {
        PicUrl = picUrl;
    }

    public String getMediaId() {
        return MediaId;
    }

    public void setMediaId(String mediaId) {
        MediaId = mediaId;
    }
}
