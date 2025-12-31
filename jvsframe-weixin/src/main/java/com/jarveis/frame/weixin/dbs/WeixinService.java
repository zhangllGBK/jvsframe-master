package com.jarveis.frame.weixin.dbs;

import com.jarveis.frame.dbs.DbsCache;
import com.jarveis.frame.dbs.Service;
import com.jarveis.frame.util.Param;
import com.jarveis.frame.weixin.WeixinCache;
import com.jarveis.frame.weixin.WeixinMsgType;
import com.jarveis.frame.weixin.WeixinRouter;
import com.jarveis.frame.weixin.WeixinUtils;
import com.jarveis.frame.weixin.bean.InputMessage;
import com.jarveis.frame.weixin.bean.OutputMessage;
import com.jarveis.frame.weixin.handler.EventHandler;
import com.jarveis.frame.weixin.handler.KeywordHandler;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * 微信服务
 * <pre>
 *     如果需要二次开发，可以对以下方法进行重写：
 *     getToken()
 *          如果公众号的token不是写在配置文件中，而是写在数据库或其它地方，可以对此方法进行重写，返回当前公众号的token
 *     initRouter()
 *          如果默认的消息路由规则不能满足自身需求，可以对此方法进行重写；定义自己的消息路由规则。
 * </pre>
 *
 * @author liuguojun
 * @since 2021-06-07
 */
public class WeixinService implements Service {

    private static final Logger log = LoggerFactory.getLogger(WeixinService.class);

    public static final WeixinRouter weixinRouter = new WeixinRouter();

    @Override
    public Param callService(Param in) {
        if (weixinRouter.isEmpty()) {
            initRouter();
        }
        String method = in.getHead().getString(Param.LABEL_METHOD);
        if (HttpMethod.GET.toString().equalsIgnoreCase(method)) {
            return doGet(in);
        } else {
            return doPost(in);
        }
    }

    /**
     * 初始化路由器
     */
    protected void initRouter() {
        weixinRouter.rule().setMsgType(WeixinMsgType.TEXT).setWeixinHandler(new KeywordHandler()).end();
        weixinRouter.rule().setMsgType(WeixinMsgType.EVENT).setWeixinHandler(new EventHandler()).end();
    }

    /**
     * 获取token
     *
     * @return String
     */
    protected String getToken() {
        return WeixinCache.getConstant("token");
    }

    /**
     * @param in 输入对象
     * @return Param
     */
    protected Param doPost(Param in) {
        Param out = null;

        try {
            out = new Param(Param.RESP);

            byte[] bytes = DbsCache.getStream();
            String weixinXML = new String(bytes, "utf-8");
            InputMessage inputMessage = WeixinUtils.getInputMessage(weixinXML);
            OutputMessage outputMessage = weixinRouter.route(inputMessage);
            out.getBody().addCDATA(outputMessage.toWeixin());
        } catch (Exception ex) {
            if (out != null) {
                out.getHead().setProperty(Param.LABEL_ERROR, Param.ERROR_EXCEPTION);
                out.getBody().addCDATA("error");
            }
            log.error(ex.getMessage(), ex);
        }

        return out;
    }

    /**
     * @param in 输入对象
     * @return Param
     */
    protected Param doGet(Param in) {
        Param out = null;
        try {
            out = new Param(Param.RESP);
            String token = getToken();
            if (StringUtils.isEmpty(token)) {
                out.getBody().addCDATA("error");
                return out;
            }
            String timestamp = in.getBody().getString("@timestamp");
            String echostr = in.getBody().getString("@echostr");
            String nonce = in.getBody().getString("@nonce");
            String signature = in.getBody().getString("@signature");
            if (!checkSignature(token, signature, timestamp, nonce)) {
                out.getBody().addCDATA("error");
                return out;
            }
            out.getBody().addCDATA(echostr);
        } catch (Exception ex) {
            if (out != null) {
                out.getHead().setProperty(Param.LABEL_ERROR, Param.ERROR_EXCEPTION);
                out.getBody().addCDATA("error");
            }
            log.error(ex.getMessage(), ex);
        }
        return out;
    }

    /**
     * 验证签名
     *
     * @param token 令牌
     * @param signature 签名
     * @param timestamp 时间戳
     * @param nonce 随机数
     * @return boolean
     * @throws NoSuchAlgorithmException 校验异常
     */
    protected boolean checkSignature(String token, String signature,
                                     String timestamp, String nonce) throws NoSuchAlgorithmException {
        log.info("token=" + token + ", signature=" + signature + ", timestamp=" + timestamp + ", nonce=" + nonce);

        String[] params = {token, timestamp, nonce};
        Arrays.sort(params);
        StringBuilder paramsBuf = new StringBuilder();
        for (String param : params) {
            paramsBuf.append(param);
        }
        MessageDigest sha = MessageDigest.getInstance("SHA-1");
        sha.update(paramsBuf.toString().getBytes());
        byte[] codeBytes = sha.digest();
        String codeStr = new BigInteger(1, codeBytes).toString(16);
        log.info("code=" + codeStr);

        return codeStr.equals(signature);
    }

}
