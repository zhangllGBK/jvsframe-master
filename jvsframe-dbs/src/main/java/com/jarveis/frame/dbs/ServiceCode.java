package com.jarveis.frame.dbs;

/**
 * 系统服务编号类
 *
 * @author liuguojun
 * @since 2019-11-19 　
 */
public interface ServiceCode {

	/**
	 * 注册服务
	 */
	String DBS_REGISTRY_SERVICE = "00001";

	/**
	 * 服务仪表盘
	 */
	String DBS_DASH_BOARD_SERVICE = "00002";

	/**
	 * 服务详情
	 */
	String DBS_SERVICE_VIEW = "00003";

	/**
	 * 任务仪表盘
	 */
	String TASK_DASH_BOARD_SERVICE = "00004";

	/**
	 * 改变任务状态
	 */
	String TASK_STATUS_CHANGE_SERVICE = "00005";

	/**
	 * 风控仪表盘
	 */
	String SLUICE_DASH_BOARD_SERVICE = "00006";

	/**
	 * 添加风控数据
	 */
	String SLUICE_CREATE_SERVICE = "00007";

	/**
	 * 删除风控数据
	 */
	String SLUICE_REMOVE_SERVICE = "00008";

	/**
	 * 发布风控数据
	 */
	String SLUICE_PUBLISH_SERVICE = "00009";

	/**
	 * 同步发布任务数据
	 */
	String TASK_PUBLISH_SERVICE = "00010";

	/**
	 * 发布任务数据展示
	 */
	String TASK_PUBLISH_SHOW_SERVICE = "00011";

	/**
	 * 发布任务数据保存
	 */
	String TASK_PUBLISH_SAVE_SERVICE = "00012";

	/**
	 * 发布任务数据删除
	 */
	String TASK_PUBLISH_DEL_SERVICE = "00013";
}