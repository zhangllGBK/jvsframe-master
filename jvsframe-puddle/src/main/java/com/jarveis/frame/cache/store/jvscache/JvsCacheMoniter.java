package com.jarveis.frame.cache.store.jvscache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 缓存监控，防止缓存空间大于用户定义的缓存空间
 *
 * @author liuguojun
 * @since 2024-03-08
 */
public class JvsCacheMoniter extends Thread {

    private static final Logger log = LoggerFactory.getLogger(JvsCacheMoniter.class);

    /**
     * 记录缓存key的最后一次访问时间
     */
    private ConcurrentHashMap<Object, Long> keyMap;

    /**
     * 检查因子（当缓存空间大于阀值时开始处理）
     */
    private final float checkFactor = 0.8f;

    /**
     * 释放因子（释放掉不常用的缓存）
     */
    private final float freeFactor = 0.1f;

    private JvsCache cache;

    public JvsCacheMoniter(JvsCache cache) {
        this.cache = cache;
        this.keyMap = new ConcurrentHashMap();
    }

    private boolean isMonit() {
        return JvsCacheManager.getMaxSize() > 0;
    }

    /**
     * 添加监控的键
     *
     * @param key
     */
    public void addMonit(Object key) {
        if (isMonit()) {
            keyMap.put(key, System.currentTimeMillis());
        }
    }

    /**
     * 检查缓存是否到达处理阀值
     *
     * @return
     */
    private boolean isFull() {
        if (isMonit()) {
            return cache.getSize() >= JvsCacheManager.getMaxSize() * checkFactor;
        }
        return false;
    }

    /**
     * 获取要释放的缓存数
     *
     * @return
     */
    private int getFreeNum() {
        return (int)(JvsCacheManager.getMaxSize() * freeFactor);
    }

    /**
     * 返回按值排序的列表
     *
     * @return
     */
    private List<Map.Entry<Object, Long>> sortKeys() {
        List<Map.Entry<Object, Long>> entryList = new ArrayList<Map.Entry<Object, Long>>(keyMap.entrySet());
        Collections.sort(entryList, new Comparator<Map.Entry<Object, Long>>() {
            @Override
            public int compare(Map.Entry<Object, Long> me1, Map.Entry<Object, Long> me2) {
                return (int)(me1.getValue().longValue() - me2.getValue().longValue()); // 升序排序
            }
        });

        return entryList;
    }

    public void run() {
        while (isMonit()) {
            try {
                if (isFull()) {
                    // 重新创建binlog文件
                    List<Map.Entry<Object, Long>> entryList = sortKeys();
                    int freeNum = getFreeNum();
                    int i = 0;
                    for (Map.Entry<Object, Long> entry : entryList) {
                        if (i > freeNum) {
                           break;
                        }
                        cache.remove(entry.getKey());
                        i++;
                    }
                }

                // 暂停1秒
                Thread.sleep(1000l);
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
            }
        }
    }
}
