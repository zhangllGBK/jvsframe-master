package com.jarveis.frame.dbs;

/**
 * 配置常量
 * @author liuguojun
 * @since  2019-12-20 　
 */
public interface DbsConst {

	/**
	 * <pre>
	 * dbs节点类型，每个节点只能设置一种类型.选项：serviceServer | serviceNode
	 *
	 * 1. 必须
	 * </pre>
	 */
	String DBS_NODE = "dbs_node";

	String DBS_NODE_SERVICE = "serviceNode";

	String DBS_NODE_SERVER = "serviceServer";

	/**
	 * <pre>
	 * dbs版本号
	 *
	 * 1. 配置适用于，dbs_node="serviceNode | serviceServer"
	 * </pre>
	 */
	String DBS_VERSION = "dbs_version";
	
	/**
	 * <pre>
	 * dbs节点编号(三位数值:000~999)
	 *
	 * 1. 配置适用于，dbs_node="serviceNode | serviceServer"
	 * 2. 在创建requestId时会用到.服务集群时，用于区分请求所在的节点；
	 * 3. 系统默认值：001
	 * 4. 必须
	 * </pre>
	 */
	String DBS_MACHINE = "dbs_machine";

	/**
	 * <pre>
	 * dbs的服务包
	 * </pre>
	 */
	String DBS_SCAN_PACKAGE = "dbs_scan_package";

	/**
	 * 总体请求限制数
	 *
	 * 1. 配置适用于，dbs_node="serviceNode | serviceServer"
	 */
	String DBS_LIMIT_REQUEST = "dbs_limit_request";

	/**
	 * 每个ip请求的限制数
	 *
	 * 1. 配置适用于，dbs_node="serviceNode | serviceServer"
	 */
	String DBS_LIMIT_IP_REQUEST = "dbs_limit_ip_request";

	/**
	 * 允许访问的IP
	 *
	 * 1. 配置适用于，dbs_node="serviceNode | serviceServer"
	 */
	String DBS_IP_ALLOW = "dbs_ip_allow";

	/**
	 * 拒绝访问的IP
	 *
	 * 1. 配置适用于，dbs_node="serviceNode | serviceServer"
	 */
	String DBS_IP_DENY = "dbs_ip_deny";

	/**
	 * <pre>
	 * 当前服务地址
	 *
	 * 1. 配置适用于，dbs_node="serviceNode | serviceServer"
	 * 2. 当前节点的服务要发布到serviceServer，供其它serviceNode进行订阅；其它serviceNode则通过本配置访问当前serviceNode下的服务。
	 * 3. 集群，必须
	 * 4. 为了兼容2.x版本，只有dbs_remote配置时生效
	 * </pre>
	 */
	String DBS_LOCAL = "dbs_local";

	/**
	 * <pre>
	 * dbs服务管理服务器，支持配置多个serviceServer地址，
	 *
	 * 1. 配置适用于，dbs_node="serviceNode | serviceServer"
	 * 2. 当前serviceNode要将本地服务供其它serviceNode进行调用时，需要将当前的serviceNode发布到serviceServer上进行管理
	 * 3. 当前serviceNode也可以从serviceServer上订阅其它远程的服务
	 * 4. 当前serviceNode也可以从serviceServer上订阅风控规则
	 * 5. serviceServer之间可以进行风控规则的同步
	 * 6. 集群，必须
	 * </pre>
	 */
	String DBS_SERVICE_SERVER = "dbs_name_server";

	/**
	 * <pre>
	 * 1. 配置适用于，dbs_node="serviceNode"
	 * 2. dbs服务模式，可同时设置两种模式(publish,subscribe)，也可任选其一。
	 *   publish--发布本地服务到serviceServer供其它service节点调用
	 *   subscribe--从serviceServer上获取远程服务并注册到本地
	 * 3. 集群，必须
	 * 4. 3.0.1版本之后不建议使用
	 * </pre>
	 */
	@Deprecated
	String DBS_SERVICE_PATTERN = "dbs_service_pattern";


	/**
	 * <pre>
	 * dbs节点要发布的服务
	 *
	 * 1. 配置适用于，dbs_node="serviceNode"
	 * 2. 空字符串、是不发布本地服务；＊、表示发布本地所有服务；支持正则表达式
	 * 3. 集群，必须
	 * </pre>
	 *
	 * @since 2020-06-30
	 */
	String DBS_PUBLISH = "dbs_publish";

	/**
	 * <pre>
	 * dbs节点要订阅的服务
	 *
	 * 1. 配置适用于，dbs_node="serviceNode"
	 * 2. 空字符串、是不订阅远程服务；＊、表示订阅远程所有服务；支持正则表达式
	 * 3. 集群，必须
	 * </pre>
	 *
	 * @since 2020-06-30
	 */
	String DBS_SUBSCRIBE = "dbs_subscribe";

	/**
	 * <pre>
	 * 心跳时间
	 *
	 * 1. 配置适用于，dbs_node="serviceNode"
	 * 2. serviceNode每间隔指定时间向serviceServer发送本地服务的同步请求
	 * 3. 系统默认时间：10000ms
	 * 4. 集群，必须
	 * </pre>
	 */
	String DBS_HEART_BEAT_TIME = "dbs_heart_beat_time";

	/**
	 * <pre>
	 * 自检时间
	 *
	 * 1. 配置适用于，dbs_node="serviceNode | serviceServer"
	 * 2. DbsService每隔100ms(系统默认)，会自检本地订阅的远程服务，如果远程服务在指定的时间内没有活跃（心跳）,则将远程服务从订阅服务中剔出
	 * 3. 为防止异常情况：dbs_self_check_time > dbs_heart_beat_time
	 * 4. 系统默认时间：30000ms
	 * 5. 集群，必须
	 * </pre>
	 */
	String DBS_SELF_CHECK_TIME = "dbs_self_check_time";

	/**
	 * <pre>
	 * 远程服务调用的超时时间
	 *
	 * 1. 配置适用于，dbs_node="serviceNode"
	 * 2. 本地serviceNode调用其它serviceNode上的服务时，防止调用时间过长导致本地serviceNode挂死
	 * 3. 默认时间：5000ms
	 * 4. 集群，非必须
	 * </pre>
	 */
	String DBS_REMOTE_SERVICE_TIMEOUT = "dbs_remote_service_timeout";

	/**
	 * <pre>
	 * 远程服务调用失败后的重试次数
	 *
	 * 1. 配置适用于，dbs_node="serviceNode"
	 * 2. 本地serviceNode调用其它serviceNode上的服务时，如果不能调用成功，有可以是网络因素造成，则加入重试机制。
	 * 3. 默认次数：1
	 * 4. 集群，非必须
	 * </pre>
	 */
	String DBS_REMOTE_SERVICE_RETRY = "dbs_remote_service_retry";

	/**
	 * dbs访问远程服务是否需要代理
	 *
	 * 1. 配置适用于，dbs_node="serviceNode"
	 * 2. 集群，非必须
	 *
	 * @since 2020-06-23
	 */
	String DBS_ISPROXY = "dbs_isproxy";
	/**
	 * @date 2021-11-11
	 * @desc 系统是否支持文件上传
	 * <pre>
	 *     "true":支持
	 *     "false":不支持
	 *     默认支持；
	 * </pre>
	 * */
	String DBS_FILE_UPLOAD_FLAG="dbs_file_upload_flag";

	/**
	 * 配置数据加密类
	 * @since 2024-08-19
	 */
	String DBS_security_class="dbs_security_class";
}
