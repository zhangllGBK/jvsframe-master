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
 * 创建客服账户
 * <pre>
 *     接口文档：
 *     <a href="https://developers.weixin.qq.com/doc/offiaccount/Customer_Service/Customer_Service_Management.html">...</a>
 *
 *     输出：
 *
 * </pre>
 *
 * @author liuguojun
 * @see WeixinURL#LIST_CUSTOM_KFACCOUNT
 * @since 2021-06-16
 */
public class KfAccountCreateService implements Service, WeixinCallback, WeixinAccessToken {

    private static final Logger log = LoggerFactory.getLogger(KfAccountCreateService.class);

    @Override
    public Param callService(Param in) {
        Param out = null;
        try {
            out = new Param(Param.RESP);
            String kf_account = in.getBody().getString("@kf_account"); // 客服账户
            String nickname = in.getBody().getString("@nickname"); // 客服昵称
            if (StringUtils.isAnyEmpty(kf_account, nickname)) {
                out.getHead().setProperty(Param.LABEL_ERROR, Param.ERROR_EXCEPTION);
                return out;
            }

            // 获取accessToken
            String accessToken = getAccessToken(in);

            String json = "{\"kf_account\" : \"%s\",\"nickname\" : \"%s\"}";

            // 请求接口
            String postURL = String.format(WeixinURL.ADD_CUSTOM_KFACCOUNT, accessToken);
            // 请求参数
            String postJson = String.format(json, kf_account, nickname);
            String result = HttpUtil.doPost(postURL, postJson);
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
