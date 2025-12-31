package com.jarveis.frame.weixin;

import com.alibaba.fastjson.JSONObject;
import com.jarveis.frame.bean.BeanUtil;
import com.jarveis.frame.util.HttpUtil;
import com.jarveis.frame.util.JsonUtil;
import com.jarveis.frame.util.Param;
import com.jarveis.frame.weixin.bean.*;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.List;

/**
 * 消息工厂类
 *
 * @author liuguojun
 * @since 2021-06-04
 */
public class WeixinUtils {

    /**
     * 解析xml为输入消息
     *
     * @param xml xml字符串
     * @return InputMessage
     */
    public static InputMessage getInputMessage(String xml) {
        InputMessage in = new InputMessage();
        try {
            Document doc = DocumentHelper.parseText(xml);
            Element element = doc.getRootElement();

            String msgType = element.elementText("MsgType");
            if (WeixinMsgType.TEXT.toString().equalsIgnoreCase(msgType)) {
                in = new TextInputMessage();
            } else if (WeixinMsgType.IMAGE.toString().equalsIgnoreCase(msgType)) {
                in = new ImageInputMessage();
            } else if (WeixinMsgType.VOICE.toString().equalsIgnoreCase(msgType)) {
                in = new VoiceInputMessage();
            } else if (WeixinMsgType.VIDEO.toString().equalsIgnoreCase(msgType)) {
                in = new VideoInputMessage();
            } else if (WeixinMsgType.SHORTVIDEO.toString().equalsIgnoreCase(msgType)) {
                in = new VideoInputMessage();
            } else if (WeixinMsgType.LOCATION.toString().equalsIgnoreCase(msgType)) {
                in = new LocationInputMessage();
            } else if (WeixinMsgType.LINK.toString().equalsIgnoreCase(msgType)) {
                in = new LinkInputMessage();
            } else if (WeixinMsgType.EVENT.toString().equalsIgnoreCase(msgType)) {
                in = new EventInputMessage();
            }

            List<Element> elements = element.elements();
            for (Element ele : elements) {
                String ename = ele.getName();
                if ("MsgID".equals(ename)) {
                    ename = "MsgId";
                }
                String evalue = ele.getTextTrim();
                if ("MsgType".equals(ename) && WeixinMsgType.SHORTVIDEO.toString().equalsIgnoreCase(evalue)) {
                    evalue = WeixinMsgType.VIDEO.toString().toLowerCase();
                }
                BeanUtil.setFieldValue(in, ename, evalue);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return in;
    }

    /**
     * 将输入消息的用户角色 转化为 输出消息的用户角色
     *
     * @param inputMessage 输入消息
     * @param outputMessage 输出消息
     */
    public static void setOutputMessage(InputMessage inputMessage, OutputMessage outputMessage){
        outputMessage.setToUserName(inputMessage.getFromUserName());
        outputMessage.setFromUserName(inputMessage.getToUserName());
        outputMessage.setCreateTime(System.currentTimeMillis());
    }

    /**
     * 获取accessToken
     *
     * @param refresh 是否重新获取；为true时，重新获取
     * @return String
     */
    public static String getAccessToken(boolean refresh){
        String appid = WeixinCache.getConstant("appid");
        String secret = WeixinCache.getConstant("secret");
        String accessToken = WeixinCache.getConstant("access_token");
        if (refresh || StringUtils.isEmpty(accessToken)) {
            String url = String.format(WeixinURL.GET_ACCESS_TOKEN, appid, secret);
            String resultJson = HttpUtil.doGet(url);
            JSONObject jsonObject = (JSONObject) JsonUtil.parse(resultJson);
            accessToken = (String)jsonObject.get("access_token");
            setAccessToken(accessToken);
        }

        return accessToken;
    }

    /**
     * 设置accessToken
     *
     * @param accessToken 访问令牌
     */
    public static void setAccessToken(String accessToken){
        WeixinCache.putConstant("access_token", accessToken, 7200);
    }

    /**
     * 封装返回数据包
     *
     * @param jsonObject json对象
     * @param out 输出对象
     */
    public static void packOutParam(JSONObject jsonObject, Param out){
        Object obj = jsonObject.get("errcode");
        if (obj == null) {
            out.getHead().setProperty(Param.LABEL_ERROR, Param.ERROR_SUCCESS);
            out.getBody().setPropertys(jsonObject);
        } else {
            String errcode = String.valueOf(obj);
            if ("0".equals(errcode)) {
                out.getHead().setProperty(Param.LABEL_ERROR, Param.ERROR_SUCCESS);
            } else {
                out.getHead().setProperty(Param.LABEL_ERROR, errcode);
            }
        }
    }


    public static void main(String[] args) {
        String xml = "<xml><ToUserName><![CDATA[gh_bc4963f49f9f]]></ToUserName><FromUserName><![CDATA[ol0zewO7wmapWISe43Qmruo4ygjI]]></FromUserName><CreateTime>1622773747</CreateTime><MsgType><![CDATA[text]]></MsgType><Content><![CDATA[E6808EE4B988E68A95E6B3A8]]></Content><MsgId>23232530945201219</MsgId></xml>";
        InputMessage in = getInputMessage(xml);
        System.out.println("in=" + in);
    }
}
