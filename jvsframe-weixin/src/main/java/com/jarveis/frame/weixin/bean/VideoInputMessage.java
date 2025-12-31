package com.jarveis.frame.weixin.bean;

/**
 * 视频输入消息
 *
 * @author liuguojun
 * @since 2021-06-01
 */
public class VideoInputMessage extends InputMessage {

    private String MediaId;
    private String ThumbMediaId;

    public String getMediaId() {
        return MediaId;
    }

    public void setMediaId(String mediaId) {
        MediaId = mediaId;
    }

    public String getThumbMediaId() {
        return ThumbMediaId;
    }

    public void setThumbMediaId(String thumbMediaId) {
        ThumbMediaId = thumbMediaId;
    }
}
