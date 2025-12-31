package com.jarveis.frame.weixin.handler;

import com.jarveis.frame.weixin.WeixinHandler;
import com.jarveis.frame.weixin.WeixinUtils;
import com.jarveis.frame.weixin.bean.InputMessage;
import com.jarveis.frame.weixin.bean.KefuOutputMessage;
import com.jarveis.frame.weixin.bean.OutputMessage;
import com.jarveis.frame.weixin.bean.TextInputMessage;

/**
 * 转发用户处理
 *
 * @author liuguojun
 * @since 2021-06-16
 */
public class TransferCustomHandler implements WeixinHandler {

    @Override
    public OutputMessage execute(InputMessage inputMessage) {
        KefuOutputMessage om = new KefuOutputMessage();
        TextInputMessage im = (TextInputMessage) inputMessage;

        WeixinUtils.setOutputMessage(im, om);

        return om;
    }

}
