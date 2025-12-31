package com.jarveis.frame.weixin;

/**
 * 微信API
 *
 * @author liuguojun
 * @since 2021-06-08
 */
public interface WeixinURL {

    /**
     * 获取
     */
    String GET_ACCESS_TOKEN = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";
    
    /**
     * 添加客服账户
     */
    String ADD_CUSTOM_KFACCOUNT = "https://api.weixin.qq.com/customservice/kfaccount/add?access_token=%s";
    /**
     * 修改客服账户
     * */
    String UPDATE_CUSTOM_KFACCOUNT="https://api.weixin.qq.com/customservice/kfaccount/update?access_token=%s";
    /**
     *
     * **/
    String SET_CUSTOM_KFACCOUNT_HEADIMG="https://api.weixin.qq.com/customservice/kfaccount/uploadheadimg?access_token=%s&kf_account=%s";
    /**
     * 邀请微信用户绑定客服账户
     */
    String INVITE_CUSTOM_KFACCOUNT = "https://api.weixin.qq.com/customservice/kfaccount/inviteworker?access_token=%s";

    /**
     * 获取客服账户列表
     */
    String LIST_CUSTOM_KFACCOUNT = "https://api.weixin.qq.com/cgi-bin/customservice/getkflist?access_token=%s";

    /**
     * 删除客服账户
     */
    String DELETE_CUSTOM_KFACCOUNT = "https://api.weixin.qq.com/customservice/kfaccount/del?access_token=%s&kf_account=%s";

    /**
     * 发送消息
     */
    String SEND_CUSTOM_MESSAGE = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=%s";

    /**
     * 获取消息列表
     */
    String LIST_CUSTOM_MESSAGE = "https://api.weixin.qq.com/customservice/msgrecord/getmsglist?access_token=%s";

    /**
     * 创建客服会话
     */
    String CREATE_CUSTOM_KFSESSION = "https://api.weixin.qq.com/customservice/kfsession/create?access_token=%s";

    /**
     * 关闭客服会话
     */
    String CLOSE_CUSTOM_KFSESSION = "https://api.weixin.qq.com/customservice/kfsession/close?access_token=%s";

    /**
     * 获取当前用户与客服的会话
     */
    String GET_CUSTOM_KFSESSION = "https://api.weixin.qq.com/customservice/kfsession/getsession?access_token=%s&openid=%s";



    /**
     * 获取用户信息
     */
    String GET_USER_INFO = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=%s&openid=%s&lang=%s";

    /**
     * 批量获取用户信息
     */
    String BATCHGET_USER_INFO = "https://api.weixin.qq.com/cgi-bin/user/info/batchget?access_token=%s";

    /**
     * 获取用户列表
     */
    String LIST_USER = "https://api.weixin.qq.com/cgi-bin/user/get?access_token=%s&next_openid=%s";

    /**
     * 设置用户备注
     */
    String UPDATE_USER_REMARK = "https://api.weixin.qq.com/cgi-bin/user/info/updateremark?access_token=%s";



    /**
     * （批量）添加黑名单
     */
    String ADD_BLACKLIST = "https://api.weixin.qq.com/cgi-bin/tags/members/batchblacklist?access_token=%s";

    /**
     * （批量）删除黑名单
     */
    String DELETE_BLACKLIST = "https://api.weixin.qq.com/cgi-bin/tags/members/batchunblacklist?access_token=%s";

    /**
     * 获取黑名单列表
     */
    String LIST_BLACKLIST = "https://api.weixin.qq.com/cgi-bin/tags/members/getblacklist?access_token=%s";



    /**
     * 添加标签
     */
    String ADD_TAG = "https://api.weixin.qq.com/cgi-bin/tags/create?access_token=%s";

    /**
     * 标签列表
     */
    String LIST_TAG = "https://api.weixin.qq.com/cgi-bin/tags/get?access_token=%s";

    /**
     * 更新标签
     */
    String UPDATE_TAG = "https://api.weixin.qq.com/cgi-bin/tags/update?access_token=%s";

    /**
     * 删除标签
     */
    String DELETE_TAG = "https://api.weixin.qq.com/cgi-bin/tags/delete?access_token=%s";

    /**
     * 获取标签下的用户列表
     */
    String LIST_TAG_USER = "https://api.weixin.qq.com/cgi-bin/user/tag/get?access_token=%s";

    /**
     * 添加标签下的用户
     */
    String ADD_TAG_USER = "https://api.weixin.qq.com/cgi-bin/tags/members/batchtagging?access_token=%s";

    /**
     * 删除标签下的用户
     */
    String DELETE_TAG_USER = "https://api.weixin.qq.com/cgi-bin/tags/members/batchuntagging?access_token=%s";

    /**
     * 获取用户下的标签列表
     */
    String LIST_USER_TAG = "https://api.weixin.qq.com/cgi-bin/tags/getidlist?access_token=%s";



    /**
     * 上传临时素材
     */
    String UPLOAD_MEDIA = "https://api.weixin.qq.com/cgi-bin/media/upload?access_token=%s&type=%s";

    /**
     * 获取临时素材
     */
    String GET_MEDIA = "https://api.weixin.qq.com/cgi-bin/media/get?access_token=%s&media_id=%s";

}
