package com.jarveis.frame.weixin.handler;

import com.jarveis.frame.weixin.WeixinHandler;
import com.jarveis.frame.weixin.WeixinUtils;
import com.jarveis.frame.weixin.bean.EventInputMessage;
import com.jarveis.frame.weixin.bean.InputMessage;
import com.jarveis.frame.weixin.bean.OutputMessage;
import com.jarveis.frame.weixin.bean.SuccessOutputMessage;

/**
 * 转发用户处理
 *
 * @author liuguojun
 * @since 2022-07-28
 */
public class EventHandler implements WeixinHandler {

    @Override
    public OutputMessage execute(InputMessage inputMessage) {
        SuccessOutputMessage om = new SuccessOutputMessage();
        EventInputMessage im = (EventInputMessage) inputMessage;

        WeixinUtils.setOutputMessage(im, om);

        return om;
    }

}
