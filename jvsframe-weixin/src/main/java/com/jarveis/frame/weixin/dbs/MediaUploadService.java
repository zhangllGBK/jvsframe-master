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

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 上传素材
 * <pre>
 *     接口文档：
 *     <a href="https://developers.weixin.qq.com/doc/offiaccount/Asset_Management/New_temporary_materials.html">...</a>
 *
 *     输出：
 *     <Resp>
 *         <head errcode="0000"/>
 *         <body media_id="rwrhk8XdjZ3o-ED2RCkOmlC-0UtHOdOu9xU-RGQ90vrUmK0vAgVU60ozUV351X0t" created_at="1634630251" type="image"/>
 *     </Resp>
 * </pre>
 *
 * @author liuguojun
 * @see WeixinURL#UPLOAD_MEDIA
 * @since 2021-10-18
 */
public class MediaUploadService implements Service, WeixinCallback, WeixinAccessToken {

    private static final Logger log = LoggerFactory.getLogger(MediaUploadService.class);

    @Override
    public Param callService(Param in) {
        Param out = null;
        try {
            out = new Param(Param.RESP);
            String type = in.getBody().getString("@type");
            String media = in.getBody().getString("@media");
            if (StringUtils.isAnyEmpty(type, media)) {
                throw new Exception("上传的数据不能为空");
            }
            File file = new File(media);
            if (!file.exists()) {
                throw new Exception("上传的素材文件(" + file.getPath() + ")，不存在");
            }
            if (!file.isFile()) {
                throw new Exception("上传的素材文件(" + file.getPath() + ")，不是普通文件");
            }

            // 获取accessToken
            String accessToken = getAccessToken(in);

            // 请求接口
            String postURL = String.format(WeixinURL.UPLOAD_MEDIA, accessToken, type);
            Map<String, Object> postParams = new HashMap<String, Object>();
            postParams.put("media", file);
            String result = HttpUtil.doPost(postURL, postParams);
            if (StringUtils.isEmpty(result)) {
                throw new Exception("接口请求异常，" + postURL + " result=" + result);
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
