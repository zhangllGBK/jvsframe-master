package com.jarveis.frame.cache.store.jvscache;

import java.io.Serializable;

/**
 * 缓存对象
 *
 * @author liuguojun
 */
public class JvsElement implements Serializable {

    private Object key; // 键
    private Object value; // 值
    private long lat = 0l; // 最后一次访问的时间
    private int acn = 0; // 访问次数
    private int expire = -1; // 生存时间(默认1年)

    public Object getKey() {
        return key;
    }

    public void setKey(Object key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public long getLat() {
        return lat;
    }

    public void setLat() {
        this.lat = System.currentTimeMillis();
    }

    public long getAcn() {
        return acn;
    }

    public void setAcn() {
        this.acn++;
    }

    public long getExpire() {
        return expire;
    }

    public void setExpire(int ttl) {
        this.expire = expire;
    }

    /**
     * 是否过期
     *
     * @return boolean
     */
    public boolean isExpired() {
        if (expire == -1) {
            return false;
        }
        return lat + expire < System.currentTimeMillis();
    }
}
