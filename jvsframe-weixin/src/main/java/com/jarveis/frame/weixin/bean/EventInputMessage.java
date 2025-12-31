package com.jarveis.frame.weixin.bean;

/**
 * 事件输入消息
 *
 * @author liuguojun
 * @since 2021-06-03
 */
public class EventInputMessage extends InputMessage {

    private String Event;
    private String EventKey;
    private String Ticket;
    private String Latitude; // 经纬度消息必需
    private String Longitude; // 经纬度消息必需
    private String Precision; // 经纬度消息必需
    private String Status; // 模块消息必需

    public String getEvent() {
        return Event;
    }

    public void setEvent(String event) {
        Event = event;
    }

    public String getEventKey() {
        return EventKey;
    }

    public void setEventKey(String eventKey) {
        EventKey = eventKey;
    }

    public String getTicket() {
        return Ticket;
    }

    public void setTicket(String ticket) {
        Ticket = ticket;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getPrecision() {
        return Precision;
    }

    public void setPrecision(String precision) {
        Precision = precision;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
}
