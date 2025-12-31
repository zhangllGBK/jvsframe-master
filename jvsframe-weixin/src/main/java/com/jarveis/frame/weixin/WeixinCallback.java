package com.jarveis.frame.weixin;

import com.jarveis.frame.util.Param;

/**
 * 微信服务回调业务
 *
 * @author liuguojun
 * @since 2021-06-16
 */
public interface WeixinCallback {

    /**
     * 回调
     *
     * @param in 输入
     * @param out 输出
     */
    void callback(Param in, Param out);
}
