package com.jarveis.frame.weixin.bean;

/**
 * 语音输出消息
 *
 * @author liuguojun
 * @since 2021-06-01
 */
public class VoiceInputMessage extends InputMessage {

    private String MediaId;
    private String Format;
    private String Recognition;

    public String getMediaId() {
        return MediaId;
    }

    public void setMediaId(String mediaId) {
        MediaId = mediaId;
    }

    public String getFormat() {
        return Format;
    }

    public void setFormat(String format) {
        Format = format;
    }

    public String getRecognition() {
        return Recognition;
    }

    public void setRecognition(String recognition) {
        Recognition = recognition;
    }
}
