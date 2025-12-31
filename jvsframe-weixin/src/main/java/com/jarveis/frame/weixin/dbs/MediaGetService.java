package com.jarveis.frame.weixin.dbs;

import com.alibaba.fastjson.JSONObject;
import com.jarveis.frame.config.ApplicationConfig;
import com.jarveis.frame.dbs.Service;
import com.jarveis.frame.util.HttpUtil;
import com.jarveis.frame.util.JsonUtil;
import com.jarveis.frame.util.Param;
import com.jarveis.frame.weixin.WeixinAccessToken;
import com.jarveis.frame.weixin.WeixinCallback;
import com.jarveis.frame.weixin.WeixinURL;
import com.jarveis.frame.weixin.WeixinUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 获取素材
 * <pre>
 *     接口文档：
 *     <a href="https://developers.weixin.qq.com/doc/offiaccount/Asset_Management/Get_temporary_materials.html">...</a>
 *
 *     输出：
 *     <Resp>
 *         <head errcode="0000"/>
 *         <body file="C:/Users/Administrator/1634637475315001.gif"/>
 *     </Resp>
 * </pre>
 *
 * @author liuguojun
 * @see WeixinURL#GET_MEDIA
 * @since 2021-10-19
 */
public class MediaGetService implements Service, WeixinCallback, WeixinAccessToken {

    private static final Logger log = LoggerFactory.getLogger(MediaGetService.class);

    @Override
    public Param callService(Param in) {
        Param out = null;
        try {
            out = new Param(Param.RESP);
            String mediaId = in.getBody().getString("@media_id");

            if (StringUtils.isEmpty(mediaId)) {
                throw new Exception("素材文件Id不能为空");
            }

            // 获取accessToken
            String accessToken = getAccessToken(in);

            // 请求接口
            String getURL = String.format(WeixinURL.GET_MEDIA, accessToken, mediaId);
            String result = HttpUtil.doGet(getURL, new String[]{"image/jpeg", "image/gif"});
            if (StringUtils.isEmpty(result)) {
                throw new Exception("接口请求异常，" + getURL + " result=" + result);
            }

            // 解析返回数据
            JSONObject jsonObject = (JSONObject) JsonUtil.parse(result);
            // 封装返回数据包
            WeixinUtils.packOutParam(jsonObject, out);
            callback(in, out);

        } catch (Exception ex) {
            if (out != null) {
                out.getHead().setProperty(Param.LABEL_ERROR, Param.ERROR_EXCEPTION);
                out.getBody().addCDATA("error");
            }
            log.error(ex.getMessage(), ex);
        }
        return out;
    }

    @Override
    public void callback(Param in, Param out) {

    }

    @Override
    public String getAccessToken(Param in) {
        return WeixinUtils.getAccessToken(false);
    }
}
