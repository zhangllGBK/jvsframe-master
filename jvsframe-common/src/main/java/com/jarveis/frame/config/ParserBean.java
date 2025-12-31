package com.jarveis.frame.config;

/**
 * 解析器配置信息
 *
 * @author liuguojun
 * @since 2022-06-29
 */
public class ParserBean implements Comparable<ParserBean> {

    private String clazz;
    private String file;
    private Boolean refreshLoad;
    private int order;

    public ParserBean(String clazz) {
        this.clazz = clazz;
        // 默认不加载
        setRefreshLoad(false);
        // 默认的优先级
        setOrder(9);
    }

    /**
     * 获取解析器类名
     *
     * @return String
     */
    public String getClazz() {
        return clazz;
    }

    /**
     * 设置解析器类名
     *
     * @param clazz 解析器类名
     */
    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    /**
     * 获取配置文件
     *
     * @return String
     */
    public String getFile() {
        return file;
    }

    /**
     * 设置配置文件
     *
     * @param file 文件路径
     */
    public void setFile(String file) {
        this.file = file;
    }

    /**
     * 获取主动刷新
     *
     * @return Boolean
     */
    public Boolean getRefreshLoad() {
        return refreshLoad;
    }

    /**
     * 设置主动刷新
     *
     * @param refreshLoad 是否主动刷新
     */
    public void setRefreshLoad(boolean refreshLoad) {
        this.refreshLoad = refreshLoad;
    }

    /**
     * 获取执行顺序（从小到大）
     *
     * @return int
     */
    public int getOrder() {
        return order;
    }

    /**
     * 设置执行顺序
     *
     * @param order 执行顺序
     */
    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public String toString() {
        return getClazz();
    }

    @Override
    public int compareTo(ParserBean o) {
        return this.getOrder() - o.getOrder();
    }
}
