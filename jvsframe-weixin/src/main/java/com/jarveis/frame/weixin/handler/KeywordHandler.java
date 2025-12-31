package com.jarveis.frame.weixin.handler;

import com.jarveis.frame.util.CharacterUtil;
import com.jarveis.frame.util.HttpUtil;
import com.jarveis.frame.util.Resource;
import com.jarveis.frame.weixin.WeixinCache;
import com.jarveis.frame.weixin.WeixinHandler;
import com.jarveis.frame.weixin.WeixinUtils;
import com.jarveis.frame.weixin.bean.InputMessage;
import com.jarveis.frame.weixin.bean.OutputMessage;
import com.jarveis.frame.weixin.bean.TextInputMessage;
import com.jarveis.frame.weixin.bean.TextOutputMessage;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 *
 *
 * @author liuguojun
 * @since 2021-06-09
 */
public class KeywordHandler implements WeixinHandler {

    private static final Logger log = LoggerFactory.getLogger(KeywordHandler.class);

    @Override
    public OutputMessage execute(InputMessage inputMessage) {
        TextOutputMessage om = new TextOutputMessage();
        TextInputMessage im = (TextInputMessage) inputMessage;
        WeixinUtils.setOutputMessage(im, om);

        try {
            String keyword = im.getContent();
            String value = WeixinCache.getKeyword(keyword);
            if (StringUtils.isEmpty(value)) {

                // 如果关键字未配置
                value = "";

            } else if (value.startsWith("file://")) {

                // 读取文件内容
                value = IOUtils.toString(Resource.getURL(value.substring(7)), CharacterUtil.UTF8);

            } else if (value.startsWith("http://") || value.startsWith("https://")) {

                // 构建http请求参数
                Map<String, String> params = new HashMap<>();
                params.put("ToUserName", im.getToUserName());
                params.put("FromUserName", im.getFromUserName());
                params.put("CreateTime", im.getCreateTime());
                params.put("MsgId", im.getMsgId());
                params.put("Content", im.getContent());
                params.put("Bizmsgmenuid", im.getBizmsgmenuid());
                // 调用http请求
                value = HttpUtil.doPost(value, params);

            }
            om.setContent(value);
        }catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }

        return om;
    }

}
