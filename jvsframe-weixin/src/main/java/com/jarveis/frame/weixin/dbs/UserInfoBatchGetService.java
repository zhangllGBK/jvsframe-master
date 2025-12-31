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
 *         <body>
 *             <user_info_list country="中国" qr_scene="0" subscribe="1" city="" openid="o7BHbwWA8nqkFpzszoBqvssQZp5s" sex="1" groupid="0" language="zh_CN" remark="o7BHbwWA8nqkFpzszoBqvssQZp5s" subscribe_time="1597312512" province="" subscribe_scene="ADD_SCENE_PROFILE_CARD" nickname="西西西西里" headimgurl="http://thirdwx.qlogo.cn/mmopen/yqlibqeKLGZqkMRqGapY5F4oibwXUcevLEoZyutUeeQmtFPoKLR4G59EibhPNWicqTLrj3KtUC0WpUE1CMicXPFhA0lWppcCyIY8t/132" qr_scene_str="" />
 *             <user_info_list country="中国" qr_scene="0" subscribe="1" city="" openid="o7BHbwR_jXAFGFiFkI6W-mVSq-ak" sex="1" groupid="0" language="zh_CN" remark="" subscribe_time="1598606659" province="上海" subscribe_scene="ADD_SCENE_PROFILE_CARD" nickname="LXH" headimgurl="http://thirdwx.qlogo.cn/mmopen/X6Ucic5kYIBMJPRsS0WCkLiaFFFuhKAnAlr3WzqUGic108lvxiavCGUmyayzF7krJeJUdyBC9V4r86tjMYtCYSEZYg/132" qr_scene_str="" />
 *         </body>
 *     </Resp>
 * </pre>
 *
 * @author liuguojun
 * @see WeixinURL#BATCHGET_USER_INFO
 * @since 2021-10-18
 */
public class UserInfoBatchGetService implements Service, WeixinCallback, WeixinAccessToken {

    private static final Logger log = LoggerFactory.getLogger(UserInfoBatchGetService.class);

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
            String[] arr = StringUtils.split(openid, ",");
            StringBuilder json = new StringBuilder("{");
            json.append("\"user_list\": [");
            for (int i = 0; i < arr.length; i++) {
                json.append("{");
                json.append("\"openid\": \"").append(arr[i]).append("\",");
                json.append("\"lang\": \"").append(lang).append("\"");
                json.append("}");
                if (i < arr.length - 1) {
                    json.append(",");
                }
            }
            json.append("]");
            json.append("}");

            // 获取accessToken
            String accessToken = getAccessToken(in);

            // 请求接口
            String getURL = String.format(WeixinURL.BATCHGET_USER_INFO, accessToken);
            String result = HttpUtil.doPost(getURL, json.toString());
            if (StringUtils.isEmpty(result)) {
                out.getHead().setProperty(Param.LABEL_ERROR, Param.ERROR_EXCEPTION);
                return out;
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
