package com.jarveis.frame.dbs;

/**
 * @Description: 数据加密接口
 * @author liuguojun
 * @since 2024-08-19
 */
public interface DbsSecurity {

    /**
     * 加密
     * @param str
     * @return
     */
    String encrypt(String str);

    /**
     * 解密
     * @param str
     * @return
     */
    String decrypt(String str);
}
