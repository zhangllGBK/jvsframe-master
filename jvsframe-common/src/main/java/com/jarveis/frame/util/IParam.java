package com.jarveis.frame.util;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * json格式的数据包
 *
 * @author liuguojun
 * @since 2020-05-28
 */
public interface IParam {


    /**
     * 获取标签名称
     *
     * @return 当前节点的标签名称
     */
    String getTagName();

    /**
     * 获取head节点
     *
     * @return IParam对象
     * @throws ParamException Param异常
     */
    IParam getHead() throws ParamException;

    /**
     * 获取body节点
     *
     * @return IParam对象
     * @throws ParamException Param异常
     */
    IParam getBody() throws ParamException;

    /**
     * 获取子节点
     *
     * @param path 节点名称
     * @return IParam对象
     * @throws ParamException Param异常
     */
    IParam getChild(String path) throws ParamException;

    /**
     * 获取子节点列表
     *
     * @param path 节点名称
     * @return IParam集合
     * @throws ParamException Param异常
     */
    List<IParam> getChilds(String path) throws ParamException;

    /**
     * 添加子节点
     *
     * @param path 节点名称
     * @return IParam对象
     * @throws ParamException Param异常
     */
    IParam addParam(String path) throws ParamException;

    /**
     * 通过节点名称删除单个节点
     *
     * @param path 节点名称
     */
    void removeChild(String path);

    /**
     * 通过节点名称删除多个节点
     *
     * @param path 节点名称
     */
    void removeChilds(String path);

    /**
     * 设置属性值
     *
     * @param path 名称
     * @param value 值
     * @throws ParamException Param异常
     */
    void setProperty(String path, Object value) throws ParamException;

    /**
     * 添加属性
     *
     * @param path 属性名
     * @param value 属性值
     */
    void setProperty(String path, boolean value);

    /**
     * 添加属性
     *
     * @param path 属性名
     * @param date 属性值
     */
    void setProperty(String path, Date date);

    /**
     * 添加属性
     *
     * @param path 属性名
     * @param decimal 属性值
     */
    void setProperty(String path, BigDecimal decimal);

    /**
     * 添加属性
     *
     * @param path 属性名
     * @param value 属性值
     */
    void setProperty(String path, float value);

    /**
     * 添加属性
     *
     * @param path 属性名
     * @param value 属性值
     */
    void setProperty(String path, double value);

    /**
     * 添加属性
     *
     * @param path 属性名
     * @param value 属性值
     */
    void setProperty(String path, int value);

    /**
     * 添加属性
     *
     * @param path 属性名
     * @param value 属性值
     */
    void setProperty(String path, long value);

    /**
     * 添加属性
     *
     * @param path 属性名
     * @param value 属性值
     */
    void setProperty(String path, short value);

    /**
     * 添加属性
     *
     * @param path 属性名
     * @param value 属性值
     */
    void setProperty(String path, Object[] value);

    /**
     * 添加属性
     *
     * @param path 属性名
     * @param value 属性值
     */
    void setProperty(String path, String value);

    /**
     * 设置属性集
     *
     * @param map 属性集合
     * @throws ParamException Param异常
     */
    void setPropertys(Map<String, Object> map) throws ParamException;

    /**
     * 删除属性
     *
     * @param path 属性名
     */
    void removeProperty(String path);

    /**
     * 删除当前节点所有属性
     */
    void removePropertys();

    /**
     * 获取属性值
     *
     * @param path 属性
     * @return 属性值
     */
    BigDecimal getBigDecimal(String path);

    /**
     * 获取属性值, 如果不存在, 则返回默认值
     *
     * @param path 属性
     * @param defaultValue 默认值
     * @return 属性值
     */
    BigDecimal getBigDecimal(String path, String defaultValue);

    /**
     * 获取属性值
     *
     * @param path 属性
     * @return 属性值
     */
    Date getDate(String path);

    /**
     * 获取属性值, 如果不存在, 则返回默认值
     *
     * @param path 属性
     * @param defaultValue 默认值
     * @return 属性值
     */
    Date getDate(String path, Date defaultValue);

    /**
     * 获取属性值
     *
     * @param path 属性
     * @return 属性值
     */
    boolean getBoolean(String path);

    /**
     * 获取属性值, 如果不存在, 则返回默认值
     *
     * @param path         属性名
     * @param defaultValue 默认值
     * @return 属性值
     */
    boolean getBoolean(String path, boolean defaultValue);

    /**
     * 获取属性值
     *
     * @param path 属性
     * @return 属性值
     */
    double getDouble(String path);

    /**
     * 获取属性值, 如果不存在, 则返回默认值
     *
     * @param path 属性
     * @param defaultValue 默认值
     * @return 属性值
     */
    double getDouble(String path, double defaultValue);

    /**
     * 获取属性值
     *
     * @param path 属性
     * @return 属性值
     */
    short getShort(String path);

    /**
     * 获取属性值, 如果不存在, 则返回默认值
     *
     * @param path 属性
     * @param defaultValue 默认值
     * @return 属性值
     */
    short getShort(String path, short defaultValue);

    /**
     * 获取属性值
     *
     * @param path 属性
     * @return 属性值
     */
    int getInteger(String path);

    /**
     * 获取属性值, 如果不存在, 则返回默认值
     *
     * @param path 属性
     * @param defaultValue 默认值
     * @return 属性值
     */
    int getInteger(String path, int defaultValue);

    /**
     * 获取属性值
     *
     * @param path 属性
     * @return 属性值
     */
    long getLong(String path);

    /**
     * 获取属性值, 如果不存在, 则返回默认值
     *
     * @param path 属性
     * @param defaultValue 默认值
     * @return 属性值
     */
    long getLong(String path, long defaultValue);

    /**
     * 获取属性值
     *
     * @param path 属性
     * @return 属性值
     */
    String getString(String path);

    /**
     * 获取属性值， 如果不存在返回默认值
     *
     * @param path 属性
     * @return 属性值
     */
    String getString(String path, String defvalue);

    /**
     * 获取属性信息
     *
     * @return 属性集合
     */
    Map<String, Object> getPropertys();

    /**
     * 添加CDATA
     *
     * @param content 内容
     */
    void addCDATA(String content);

    /**
     * 获取CDATA值
     *
     * @return 复合性文本值
     */
    String getCDATA();

    /**
     * 格式化为字符串
     *
     * @return Param序列化后的字符串
     */
    String formatString();

}
