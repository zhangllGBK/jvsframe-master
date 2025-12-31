package com.jarveis.frame.cache.store.jvscache;


import com.jarveis.frame.cache.Cache;
import com.jarveis.frame.cache.CacheException;
import com.jarveis.frame.cache.CacheExpiredListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 缓存类
 *
 * @author liuguojun
 * @since 2024-03-05
 */
public class JvsCache implements Cache {

    private static final Logger log = LoggerFactory.getLogger(JvsCache.class);

    private String region;
    private CacheExpiredListener listener;
    private ConcurrentHashMap<Object, JvsElement> cacheMap;
    private JvsBinLog binLog;
    private JvsCacheMoniter moniter;

    public JvsCache(String region) {
        this(region,null);
    }

    public JvsCache(String region, CacheExpiredListener listener) {
        this.region = region;
        this.listener = listener;
        this.cacheMap = new ConcurrentHashMap<>();
        // biglog线程写入操作日志
        this.binLog = new JvsBinLog(this);
        this.binLog.start();
        // 缓存监控
        this.moniter = new JvsCacheMoniter(this);
        this.moniter.start();
    }

    public void setListener(CacheExpiredListener listener) {
        this.listener = listener;
    }

    public int getSize() {
        return cacheMap == null ? 0 : cacheMap.size();
    }

    /**
     * 缓存是否已满
     * @return
     */
    private boolean isFull() {
        if (JvsCacheManager.getMaxSize() == -1) {
            return false;
        }
        return getSize() >= JvsCacheManager.getMaxSize();
    }

    /**
     * 设置缓存对象
     * <pre>
     *
     * </pre>
     *
     * @param element
     */
    private void putElement(JvsElement element) {
        if (!isFull()) {
            cacheMap.put(element.getKey(), element);
            moniter.addMonit(element.getKey());
        } else {
            try {
                throw new Exception("jvscache [" + region + "] is full");
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
            }
        }
    }

    public void put(Object key, Object value) {
        put(key, value, -1);
    }

    public void put(Object key, Object value, Integer expireInSec) {
        JvsElement element = new JvsElement();
        element.setKey(key);
        element.setValue(value);
        element.setExpire(expireInSec);
        element.setAcn();
        element.setLat();

        // 设置缓存
        putElement(element);

        // 写日志
        binLog.addCommandQueue("put", region, key, value, expireInSec);

        if (log.isDebugEnabled()) {
            log.debug(String.format("put: key=%s, value=%s, expireInSec=%d", key, value, expireInSec));
        }
    }

    public JvsElement getElement(Object key) {
        JvsElement element = cacheMap.get(key);
        if (element != null) {
            if (element.isExpired() == true) {
                element = null;
                cacheMap.remove(key);
                // 监听支持
                if (listener != null) {
                    listener.notifyElementExpired(region, key);
                }
            }
        }

        if (element == null) {
            element = new JvsElement();
            element.setKey(key);
        }
        element.setAcn();
        element.setLat();

        return element;
    }

    public Object get(Object key) {
        Object reValue;

        JvsElement element = getElement(key);
        reValue = element.getValue();
        if (reValue != null) {
            // 更新访问时间
            putElement(element);
            if (element.getExpire() != -1) {
                // 考虑到缓存读多、写少；对于永久有效的，不用把每次的读取动作写入到日志。（减少日志量，提高缓存启动时的加载效率）
                binLog.addCommandQueue("get", region, key);
            }
        }

        if (log.isDebugEnabled()) {
            log.debug(String.format("get: key=%s, value=%s", key, reValue));
        }

        return reValue;
    }

    public Object exists(Object key) {
        Boolean reValue = Boolean.valueOf("false");

        JvsElement element = getElement(key);
        Object value = element.getValue();
        if (value != null) {
            reValue = Boolean.valueOf("true");
        }

        if (log.isDebugEnabled()) {
            log.debug(String.format("get: key=%s, value=%s", key, value));
        }
        return reValue;
    }

    public void remove(Object key) {
        cacheMap.remove(key);

        // 写日志
        binLog.addCommandQueue("remove", region, key);

        if (log.isDebugEnabled()) {
            log.debug(String.format("remove: key=%s", key));
        }
    }

    public void removeAll(List keys) {
        try {
            for (Object key : keys) {
                remove(key);
            }
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    public void clear() {
        cacheMap.clear();

        // 写日志
        binLog.addCommandQueue("clear", region);

        if (log.isDebugEnabled()) {
            log.debug(String.format("clear: region=%s", region));
        }
    }

    public void destroy() {

    }

    public void lpush(Object key, Object value) {
        JvsElement element = getElement(key);
        LinkedList valueList = (LinkedList) element.getValue();
        if (valueList == null) {
            valueList = new LinkedList();
        }
        valueList.addFirst(value);

        // 刷新访问时间
        element.setValue(valueList);
        putElement(element);

        // 写日志
        binLog.addCommandQueue("lpush", region, key, value);

        if (log.isDebugEnabled()) {
            log.debug(String.format("lpush: key=%s, value=%s", key, value));
        }
    }

    public void rpush(Object key, Object value) {
        JvsElement element = getElement(key);
        LinkedList valueList = (LinkedList)element.getValue();
        if (valueList == null) {
            valueList = new LinkedList();
        }
        valueList.addLast(value);

        // 刷新访问时间
        element.setValue(valueList);
        putElement(element);

        // 写日志
        binLog.addCommandQueue("rpush", region, key, value);

        if (log.isDebugEnabled()) {
            log.debug(String.format("rpush: key=%s, value=%s", key, value));
        }
    }

    public Object lpop(Object key) {
        Object reValue = null;

        JvsElement element = getElement(key);
        LinkedList valueList = (LinkedList)element.getValue();
        if (valueList != null) {
            reValue = valueList.removeFirst();

            // 刷新访问时间
            element.setValue(valueList);
            putElement(element);
        }

        // 写日志
        binLog.addCommandQueue("lpop", region, key);

        if (log.isDebugEnabled()) {
            log.debug(String.format("lpop: key=%s, reValue=%s", key, reValue));
        }

        return reValue;
    }

    public Object rpop(Object key) {
        Object reValue = null;

        JvsElement element = getElement(key);
        LinkedList valueList = (LinkedList)element.getValue();
        if (valueList != null) {
            reValue = valueList.removeLast();

            // 刷新访问时间
            element.setValue(valueList);
            putElement(element);
        }

        // 写日志
        binLog.addCommandQueue("rpop", region, key);

        if (log.isDebugEnabled()) {
            log.debug(String.format("rpop: key=%s, reValue=%s", key, reValue));
        }

        return reValue;
    }

    public Object llen(Object key) {
        // 缓存列表长度
        int reValue = 0;

        // 缓存列表
        JvsElement element = getElement(key);
        LinkedList valueList = (LinkedList)element.getValue();
        if (valueList != null) {
            reValue = valueList.size();
        }

        if (log.isDebugEnabled()) {
            log.debug(String.format("llen: key=%s, reValue=%s", key, reValue));
        }

        return reValue;
    }

    public Object lrange(Object key, int count) {
        List<Object> reList = new ArrayList<>();

        JvsElement element = getElement(key);
        LinkedList valueList = (LinkedList)element.getValue();
        if (valueList != null) {
            for (Object value : valueList) {
                reList.add(value);
                if (reList.size() == count) {
                    break;
                }
            }
        }
        return reList;
    }

    public void lrem(Object key, int count) {
        JvsElement element = getElement(key);
        LinkedList valueList = (LinkedList)element.getValue();
        if (valueList != null) {
            while (count > 0) {
                valueList.removeFirst();
                count--;
            }

            // 刷新访问时间
            element.setValue(valueList);
            putElement(element);
        }

        // 写日志
        binLog.addCommandQueue("lrem", region, key, count);

        if (log.isDebugEnabled()) {
            log.debug(String.format("lrem: key=%s, count=%s", key, count));
        }
    }

    public Object hget(Object key, String field) {
        Object reValue = null;

        JvsElement element = getElement(key);
        Map valueMap = (Map)element.getValue();
        if (valueMap != null) {
            reValue = valueMap.get(field);

            // 刷新访问时间
            element.setValue(valueMap);
            putElement(element);
        }

        // 不用写日志，不会对缓存数据进行影响
        // binLog.addCommandQueue("hget", region, key, field);

        if (log.isDebugEnabled()) {
            log.debug(String.format("hget: key=%s, field=%s, reValue=%s", key, field, reValue));
        }

        return reValue;
    }

    public void hput(Object key, String field, Object value) {
        JvsElement element = getElement(key);
        Map valueMap = (Map)element.getValue();
        if (valueMap == null) {
            valueMap = new HashMap();
        }
        valueMap.put(field, value);

        // 刷新访问时间
        element.setValue(valueMap);
        putElement(element);

        // 写日志
        binLog.addCommandQueue("hput", region, key, field, value);

        if (log.isDebugEnabled()) {
            log.debug(String.format("hput: key=%s, field=%s, value=%s", key, field, value));
        }
    }

    public void hrem(Object key, String field) {
        JvsElement element = getElement(key);
        Map valueMap = (Map)element.getValue();
        if (valueMap != null) {
            valueMap.remove(field);

            // 刷新访问时间
            element.setValue(valueMap);
            putElement(element);
        }

        // 写日志
        binLog.addCommandQueue("hrem", region, key, field);

        if (log.isDebugEnabled()) {
            log.debug(String.format("hrem: key=%s, field=%s", key, field));
        }
    }

    public Object hexists(Object key, String field) {
        Object reValue = Boolean.valueOf("false");

        if (key != null) {
            JvsElement element = getElement(key);
            Map valueMap = (Map)element.getValue();
            if (valueMap.get(field) != null) {
                reValue = Boolean.valueOf("true");
            }
        }

        if (log.isDebugEnabled()) {
            log.debug(String.format("hexists: key=%s, field=%s, reValue=%s", key, field, reValue));
        }
        return reValue;
    }
}
