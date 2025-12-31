package com.jarveis.frame.weixin;

import com.jarveis.frame.weixin.bean.InputMessage;
import com.jarveis.frame.weixin.bean.OutputMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * 微信消息路由器
 *
 * @author liuguojun
 * @since 2021-06-07
 */
public class WeixinRouter {

    private List<WeixinRouterRule> rules;

    public WeixinRouter(){
        rules = new ArrayList<>();
    }

    /**
     * 路由规则是否为空
     *
     * @return boolean
     */
    public boolean isEmpty(){
        return rules.isEmpty();
    }

    /**
     * 创建路由规则
     *
     * @return WeixinRouterRule
     */
    public WeixinRouterRule rule() {
        return new WeixinRouterRule(this);
    }

    /**
     * 添加路由规则
     *
     * @param weixinRouterRule 路由规则
     */
    public void addRule(WeixinRouterRule weixinRouterRule) {
        this.rules.add(weixinRouterRule);
    }

    /**
     * 消息路由处理
     *
     * @param inputMessage 输入消息
     * @return OutputMessage
     */
    public OutputMessage route(InputMessage inputMessage){
        OutputMessage outputMessage = null;
        for (WeixinRouterRule rule : rules) {
            if (rule.test(inputMessage)) {
                outputMessage = rule.execute(inputMessage);
                break;
            }
        }
        return outputMessage;
    }
}
