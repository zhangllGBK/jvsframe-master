package com.jarveis.frame.weixin;

import com.jarveis.frame.util.Param;

/**
 * AccessToken获取接口
 *
 * @author liuguojun
 * @since 2021-10-21
 */
public interface WeixinAccessToken {

    /**
     * 获取微信access token
     *
     * @param in 输入
     */
    String getAccessToken(Param in);
}
