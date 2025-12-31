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
 *     输出
 *     <Resp>
 *         <head errcode="0000"/>
 *         <body total="1" count="1" next_openid="o7BHbwWA8nqkFpzszoBqvssQZp5s">
 *             <data openid="o7BHbwWA8nqkFpzszoBqvssQZp5s"/>
 *         </body>
 *     </Resp>
 * </pre>
 *
 * @author liuguojun
 * @see WeixinURL#LIST_BLACKLIST
 * @since 2021-10-18
 */
public class BlacklistQueryService implements Service, WeixinCallback, WeixinAccessToken {

    private static final Logger log = LoggerFactory.getLogger(BlacklistQueryService.class);

    @Override
    public Param callService(Param in) {
        Param out = null;
        try {
            out = new Param(Param.RESP);
            String nextOpenid = in.getBody().getString("@next_openid");

            String json = "{\"begin_openid\" : \"%s\"}";

            // 获取accessToken
            String accessToken = getAccessToken(in);

            // 请求接口
            String postURL = String.format(WeixinURL.LIST_BLACKLIST, accessToken);
            String postJson = String.format(json, nextOpenid);
            String result = HttpUtil.doPost(postURL, postJson);
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
