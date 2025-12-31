package com.jarveis.frame.weixin.dbs;

import com.alibaba.fastjson.JSONObject;
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
 * 获取用户信息
 * <pre>
 *     接口文档：
 *     <a href="https://developers.weixin.qq.com/doc/offiaccount/User_Management/Getting_a_User_List.html">...</a>
 *
 *     输出：
 *
 * </pre>
 *
 * @author liuguojun
 * @see WeixinURL#DELETE_TAG_USER
 * @since 2021-10-18
 */
public class TagUserDeleteService implements Service, WeixinCallback, WeixinAccessToken {

    private static final Logger log = LoggerFactory.getLogger(TagUserDeleteService.class);

    @Override
    public Param callService(Param in) {
        Param out = null;
        try {
            out = new Param(Param.RESP);

            String tagid = in.getBody().getString("@tagid");
            String openid = in.getBody().getString("@openid");
            if (StringUtils.isEmpty(openid)) {
                throw new Exception("openid不能为空");
            }

            String[] arr = StringUtils.split(openid, ",");
            StringBuilder json = new StringBuilder("{");
            json.append("\"tagid\":").append(tagid).append(",");
            json.append("\"openid_list\": [");
            for (int i = 0; i < arr.length; i++) {
                json.append("\"").append(arr[i]).append("\"");
                if (i < arr.length - 1) {
                    json.append(",");
                }
            }
            json.append("]");
            json.append("}");

            // 获取accessToken
            String accessToken = getAccessToken(in);

            // 请求接口
            String postURL = String.format(WeixinURL.DELETE_TAG_USER, accessToken);
            String result = HttpUtil.doPost(postURL, json.toString());
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
