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
 *         <head errcode="0000" />
 *         <body total="8" count="8" next_openid="o7BHbwZUcBIAjZy_SsesLazJAvDE">
 *             <data openid="o7BHbwWA8nqkFpzszoBqvssQZp5s,o7BHbwR_jXAFGFiFkI6W-mVSq-ak,o7BHbwfButQRnAifzzI9FiVSreYA,o7BHbwdBOh6rptovugjD97VRVSdQ,o7BHbwdlRKWZPC3GwhIqe2qlF_Uw,o7BHbwZE1wU3rweMwdbagiKBFbUU,o7BHbwaGtTRNqHjz_BdnKayPQjps,o7BHbwZUcBIAjZy_SsesLazJAvDE" />
 *         </body>
 *     </Resp>
 * </pre>
 *
 * @author liuguojun
 * @see WeixinURL#LIST_USER
 * @since 2021-10-18
 */
public class UserQueryService implements Service, WeixinCallback, WeixinAccessToken {

    private static final Logger log = LoggerFactory.getLogger(UserQueryService.class);

    @Override
    public Param callService(Param in) {
        Param out = null;
        try {
            out = new Param(Param.RESP);
            String nextOpenid = in.getBody().getString("@next_openid");

            // 获取accessToken
            String accessToken = getAccessToken(in);

            // 请求接口
            String getURL = String.format(WeixinURL.LIST_USER, accessToken, nextOpenid);
            String result = HttpUtil.doGet(getURL);
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
