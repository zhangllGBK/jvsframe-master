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

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 设置客服账号头像
 * <pre>
 *     文档地址:<a href="https://developers.weixin.qq.com/doc/offiaccount/Message_Management/Service_Center_messages.html">...</a>     --->设置客服帐号的头像
 * </pre>
 *
 * @author 小康-Xk
 * @since 2022-02-24
 */
public class KfAccountHeadimgSetService implements Service, WeixinCallback, WeixinAccessToken {

    private static final Logger log = LoggerFactory.getLogger(KfAccountHeadimgSetService.class);

    @Override
    public Param callService(Param in) {
        Param out = null;
        try {
            out = new Param(Param.RESP);
            // 图片绝对地址
            String imgUrl = in.getBody().getString("@imgurl");
            // 客服账户
            String acount = in.getBody().getString("@kf_account");
            if (StringUtils.isAnyEmpty(imgUrl, acount)) {
                out.getHead().setProperty(Param.LABEL_ERROR, Param.ERROR_EXCEPTION);
                return out;
            }
            File file = new File(imgUrl);
            if (!file.exists()) {
                out.getHead().setProperty(Param.LABEL_ERROR, Param.ERROR_EXCEPTION);
                return out;
            }
            String accessToken = getAccessToken(in);
            // 请求地址
            String postURL = String.format(WeixinURL.SET_CUSTOM_KFACCOUNT_HEADIMG, accessToken, acount);
            Map<String, Object> postParams = new HashMap<String, Object>();
            postParams.put("media", file);
            String result = HttpUtil.doPost(postURL, postParams);
            if (StringUtils.isEmpty(result)) {
                out.getHead().setProperty(Param.LABEL_ERROR, Param.ERROR_EXCEPTION);
                return out;
            }
            // 解析返回数据
            JSONObject jsonObject = (JSONObject) JsonUtil.parse(result);
            // 封装返回数据包
            WeixinUtils.packOutParam(jsonObject, out);
            callback(in, out);
        } catch (Exception e) {
            log.error(e.getMessage());
            if (out != null) {
                out.getHead().setProperty(Param.LABEL_ERROR, Param.ERROR_EXCEPTION);
                out.getBody().addCDATA("error");
            }
        }
        return out;
    }

    @Override
    public String getAccessToken(Param in) {
        return WeixinUtils.getAccessToken(false);
    }

    @Override
    public void callback(Param in, Param out) {

    }
}
