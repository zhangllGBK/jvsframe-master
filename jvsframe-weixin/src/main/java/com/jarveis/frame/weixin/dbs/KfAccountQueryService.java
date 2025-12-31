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
 * 获取客服列表
 * <pre>
 *     接口文档：
 *     <a href="https://developers.weixin.qq.com/doc/offiaccount/Customer_Service/Customer_Service_Management.html">...</a>
 *
 *     输出：
 * </pre>
 *
 * @author liuguojun
 * @see WeixinURL#LIST_CUSTOM_KFACCOUNT
 * @since 2021-06-16
 */
public class KfAccountQueryService implements Service, WeixinCallback, WeixinAccessToken {

    private static final Logger log = LoggerFactory.getLogger(KfAccountQueryService.class);

    @Override
    public Param callService(Param in) {
        Param out = null;
        try {
            out = new Param(Param.RESP);

            // 获取accessToken
            String accessToken = getAccessToken(in);

            // 请求接口
            String getURL = String.format(WeixinURL.LIST_CUSTOM_KFACCOUNT, accessToken);
            String result = HttpUtil.doGet(getURL);
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
