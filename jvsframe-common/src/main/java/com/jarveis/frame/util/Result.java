package com.jarveis.frame.util;

import com.alibaba.fastjson.annotation.JSONCreator;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * 封装返回的数据信息
 * 
 * @author liuguojun
 */
public class Result {

	private static final Logger log = LoggerFactory.getLogger(Result.class);
	
	public static String CODE_SUCCESS = "0000";
	
	private String code; // 代码
	private Object data; // 数据

	@JSONCreator
	private Result(@JSONField(name = "code") String code,
			@JSONField(name = "data") Object data) {
		this.code = code;
		this.data = data;
	}

	/**
	 * 获取返回信息中的状态码
	 * 
	 * @return String
	 */
	public String getCode() {
		return this.code;
	}

	/**
	 * 获取返回信息中的数据
	 * 
	 * @return Object
	 */
	public Object getData() {
		return this.data;
	}
	
	/**
	 * 难返回是否为正确返回
	 * 
	 * @return
	 */
	public boolean isSuccess(){
		return CODE_SUCCESS.equals(this.code);
	}
	
	/**
	 * 难返回是否为正确返回
	 * 
	 * @return
	 */
	public boolean isSuccess(String code){
		if (code == null) {
			return false;
		}
		return code.equals(this.code);
	}

	/**
	 * 获取返回的數據封裝包
	 * 
	 * @param code
	 *            结果集代码
	 * @param data
	 *            结果集
	 * @return Result
	 */
	@JSONCreator
	private static Result getInstance(@JSONField(name = "code") String code,
			@JSONField(name = "data") Object data) {
		return new Result(code, data);
	}

	/**
	 * 返回正确的结果集
	 * 
	 * @param code
	 *            结果集代码
	 * @param data
	 *            结果集
	 * @return String
	 */
	public static String format(String code, Object data) {
		return getInstance(code, data).toString();
	}

	/**
	 * 解析字符串为返回对象
	 * 
	 * @param json
	 *            json字符串
	 * @return Result
	 * @throws IOException
	 */
	public static Result parse(String json) {
		if (StringUtils.isEmpty(json)) {
			return null;
		}
		return JsonUtil.parse(json, Result.class);
	}
	
	public String toString() {
		String str = JsonUtil.toJson(this, SerializerFeature.WriteClassName);
		return str;
	}
}
