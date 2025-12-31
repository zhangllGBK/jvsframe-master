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
 *     <a href="https://developers.weixin.qq.com/doc/offiaccount/User_Management/Get_users_basic_information_UnionID.html#UinonId">...</a>
 *
 *     输出
 *     <Resp>
 *         <head errcode="0000" />
 *         <body country="中国" qr_scene="0" subscribe="1" city="" openid="o7BHbwWA8nqkFpzszoBqvssQZp5s" sex="1" groupid="0" language="zh_CN" remark="" subscribe_time="1597312512" province="" subscribe_scene="ADD_SCENE_PROFILE_CARD" nickname="西西西西里" headimgurl="http://thirdwx.qlogo.cn/mmopen/yqlibqeKLGZqkMRqGapY5F4oibwXUcevLEoZyutUeeQmtFPoKLR4G59EibhPNWicqTLrj3KtUC0WpUE1CMicXPFhA0lWppcCyIY8t/132" qr_scene_str="" />
 *     </Resp>
 * </pre>
 *
 * @author liuguojun
 * @see WeixinURL#GET_USER_INFO
 * @since 2021-10-18
 */
public class UserInfoGetService implements Service, WeixinCallback, WeixinAccessToken {

    private static final Logger log = LoggerFactory.getLogger(UserInfoGetService.class);

    @Override
    public Param callService(Param in) {
        Param out = null;
        try {
            out = new Param(Param.RESP);
            String openid = in.getBody().getString("@openid");
            String lang = in.getBody().getString("@lang", "zh_CN");
            if (StringUtils.isEmpty(openid)) {
                throw new Exception("openid不能为空");
            }

            // 获取accessToken
            String accessToken = getAccessToken(in);

            // 请求接口
            String getURL = String.format(WeixinURL.GET_USER_INFO, accessToken, openid, lang);
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
