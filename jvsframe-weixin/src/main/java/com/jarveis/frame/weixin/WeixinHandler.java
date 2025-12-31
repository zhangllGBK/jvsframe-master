package com.jarveis.frame.weixin;

import com.jarveis.frame.weixin.bean.InputMessage;
import com.jarveis.frame.weixin.bean.OutputMessage;

/**
 * 微信消息处理器
 *
 * @author liuguojun
 * @since 2021-06-07
 */
public interface WeixinHandler {

    /**
     * 处理用户消息
     *
     * @return OutputMessage
     */
    OutputMessage execute(InputMessage inputMessage);
}
