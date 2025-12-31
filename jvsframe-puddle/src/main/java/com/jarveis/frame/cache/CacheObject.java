package com.jarveis.frame.cache;

/**
 * 缓存中的缓存对象
 * 
 * @author liuguojun
 * @since 2018-08-31
 */
public class CacheObject {

	private String region;
	private Object key;
	/**
	 * 用于hash类型；key为hash类型的键，field为hash的属性
	 *
	 * @since 2020-12-17
	 */
	private String field;
	private Object value;
	private byte level; // 缓存级别
	private long lat; // 最后一次访问的时间
	private long acn; // 访问次数
	private long ttl; // 生存时间

	public Object getKey() {
		return key;
	}

	public void setKey(Object key) {
		this.key = key;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public byte getLevel() {
		return level;
	}

	public void setLevel(byte level) {
		this.level = level;
	}

	/**
	 * 是否过期
	 * 
	 * @return boolean
	 */
	public boolean isExpired() {
		if (ttl == 0) {
			return false;
		}
		return lat + ttl < System.currentTimeMillis();
	}

	/**
	 * 获取访问次数
	 * 
	 * @return long
	 */
	public long getAccessCount() {
		return acn;
	}

	/**
	 * 获取缓存中的值
	 * 
	 * @return Object
	 */
	public Object getValue() {
		lat = System.currentTimeMillis();
		acn++;
		return value;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getField() {
		return field;
	}

	@Override
	public String toString() {
		return "CacheObject [region=" + region + ", key=" + key + ", field=" + field + ", value="
				+ value + ", level=" + level + ", lat=" + lat + ", acn=" + acn
				+ ", ttl=" + ttl + "]";
	}
}
