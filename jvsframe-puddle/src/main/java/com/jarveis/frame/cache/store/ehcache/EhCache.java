package com.jarveis.frame.cache.store.ehcache;

import com.jarveis.frame.cache.Cache;
import com.jarveis.frame.cache.CacheException;
import com.jarveis.frame.cache.CacheExpiredListener;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * ehcache缓存
 *
 * @author liuguojun
 * @since 2018-08-31
 */
public class EhCache implements Cache, CacheEventListener {

	private static final Logger log = LoggerFactory.getLogger(EhCache.class);

	private String region;
	private net.sf.ehcache.Cache cache;
	private CacheExpiredListener listener;
	private final static char LIST_SEPARATOR = 0x01;

	public EhCache(net.sf.ehcache.Cache cache, CacheExpiredListener listener) {
		this.cache = cache;
		region = this.cache.getName();
		this.cache.getCacheEventNotificationService().registerListener(this);
		this.listener = listener;
	}

	private String getKey(Object key) {
		return region + ":" + key.toString();
	}

	private String getHKey(Object key, String field) {
		return region + ":" + key.toString() + "$" + field;
	}

	public void dispose() {
	}

	public void notifyElementEvicted(Ehcache arg0, Element arg1) {
	}

	public void notifyElementExpired(Ehcache arg0, Element arg1) {
		if (listener != null) {
			listener.notifyElementExpired(arg0.getName(), arg1.getObjectKey());
		}
	}

	public void notifyElementPut(Ehcache arg0, Element arg1)
			throws CacheException {
	}

	public void notifyElementRemoved(Ehcache arg0, Element arg1)
			throws CacheException {
	}

	public void notifyElementUpdated(Ehcache arg0, Element arg1)
			throws CacheException {
	}

	public void notifyRemoveAll(Ehcache arg0) {
	}

	public void put(Object key, Object value) {
		try {
			Element element = new Element(getKey(key), value);
			cache.put(element);
			if (log.isDebugEnabled()) {
				log.debug(String.format("put: key=%s, vlaue=%s", getKey(key), value));
			}
		} catch (Exception e) {
			throw new CacheException(e);
		}
	}

	public void put(Object key, Object value, Integer expireInSec) {
		try {
			Element element = new Element(getKey(key), value);
			element.setTimeToLive(expireInSec);
			cache.put(element);
			if (log.isDebugEnabled()) {
				log.debug(String.format("put: key=%s, vlaue=%s, expire=%d", getKey(key), value, expireInSec));
			}
		} catch (Exception e) {
			throw new CacheException(e);
		}
	}

	public Object get(Object key) {
		try {
			if (key == null) {
				return null;
			} else {
				Element element = cache.get(getKey(key));
				if (element != null) {
					if (log.isDebugEnabled()) {
						log.debug(String.format("get: key=%s, vlaue=%s", getKey(key), element.getObjectValue()));
					}
					return element.getObjectValue();
				}
			}
			return null;
		} catch (net.sf.ehcache.CacheException e) {
			throw new CacheException(e);
		}
	}

	public void remove(Object key) {
		try {
			cache.remove(getKey(key));
			if (log.isDebugEnabled()) {
				log.debug(String.format("remove: key=%s", getKey(key)));
			}
		} catch (Exception e) {
			throw new CacheException(e);
		}
	}

	@Override
	public Object exists(Object key) {
		try {
			if (key == null) {
				return null;
			} else {
				Element element = cache.get(getKey(key));
				if (element != null) {
					if (log.isDebugEnabled()) {
						log.debug(String.format("get: key=%s, vlaue=%s", getKey(key), element.getObjectValue()));
					}
					return Boolean.valueOf("true");
				} else {
					return Boolean.valueOf("false");
				}
			}
		} catch (net.sf.ehcache.CacheException e) {
			throw new CacheException(e);
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
		try {
			cache.removeAll();
		} catch (Exception e) {
			throw new CacheException(e);
		}

	}

	public void destroy() {
		try {
			cache.getCacheManager().removeCache(cache.getName());
		} catch (Exception e) {
			throw new CacheException(e);
		}
	}

	@Override
	public void lpush(Object key, Object value) {
		// TODO support List
		Object cacheValue = get(key);
		if (cacheValue != null) {
			value = String.valueOf(value) + LIST_SEPARATOR + String.valueOf(cacheValue);
		}
		put(key, value);
	}

	@Override
	public void rpush(Object key, Object value) {
		Object cacheValue = get(key);
		if (cacheValue != null) {
			value = String.valueOf(cacheValue) + LIST_SEPARATOR + String.valueOf(value);
		}
		put(key, value);
	}

	@Override
	public Object lpop(Object key) {
		String cacheValue = (String)get(key);
		if (cacheValue != null) {
			int idx = cacheValue.indexOf(LIST_SEPARATOR);
			if (idx > 0) {
				String reValue = cacheValue.substring(0, idx);
				put(key, cacheValue.substring(idx + 1));
				cacheValue = reValue;
			} else {
				remove(key);
			}
		}
		return cacheValue;
	}

	@Override
	public Object rpop(Object key) {
		String cacheValue = (String)get(key);
		if (cacheValue != null) {
			int idx = cacheValue.lastIndexOf(LIST_SEPARATOR);
			if (idx > 0) {
				String reValue = cacheValue.substring(idx + LIST_SEPARATOR);
				put(key, cacheValue.substring(0, idx));
				cacheValue = reValue;
			} else {
				remove(key);
			}
		}
		return cacheValue;
	}

	@Override
	public Object llen(Object key) {
		// 缓存列表长度
		int llen = 0;
		// 缓存列表
		String cacheValue = (String)get(key);
		if (StringUtils.isNotEmpty(cacheValue)) {
			String value = StringUtils.EMPTY; // 明细
			boolean isToken = false;
			int idx = 0;
			while (true) {
				// aaa
				// aaabbbccc
				char c = cacheValue.charAt(idx);
				if (c == LIST_SEPARATOR) {
					// 标记分隔，进行处理
					isToken = !isToken;
				}

				if (isToken) {
					llen++;
					// 重置
					value = StringUtils.EMPTY;
					isToken = !isToken;
				} else {
					value += c;
				}
				idx++;
				if (idx >= cacheValue.length() && StringUtils.isNotEmpty(value)) {
					llen++;
					break;
				}
			}
		}

		return llen;
	}

	@Override
	public Object lrange(Object key, int count) {
		List<String> reList = new ArrayList<String>();
		// 缓存列表长度
		int llen = 0;
		// 缓存列表
		String cacheValue = (String)get(key);
		if (StringUtils.isNotEmpty(cacheValue)) {
			String value = StringUtils.EMPTY; // 明细
			boolean isToken = false;
			int idx = 0;
			while (true) {
				// aaa
				// aaabbbccc
				char c = cacheValue.charAt(idx);
				if (c == LIST_SEPARATOR) {
					// 标记分隔，进行处理
					isToken = !isToken;
				}

				if (isToken) {
					if (count > llen) {
						reList.add(value);
					}
					llen++;
					// 重置
					value = StringUtils.EMPTY;
					isToken = !isToken;
				} else {
					value += c;
				}
				idx++;
				if (llen >= count) {
					// 当获取到指定个数的数据后，则退出
					break;
				}
				if (idx >= cacheValue.length() && StringUtils.isNotEmpty(value)) {
					if (count > llen) {
						reList.add(value);
					}
					break;
				}
			}
		}

		return reList;
	}

	@Override
	public void lrem(Object key, int count) {
		// 缓存列表长度
		int llen = 0;
		// 缓存列表
		String cacheValue = (String)get(key);
		if (StringUtils.isNotEmpty(cacheValue)) {
			String value = StringUtils.EMPTY; // 明细
			String recache = StringUtils.EMPTY; // 重置后的缓存
			boolean isToken = false;
			int idx = 0;
			while (true) {
				// aaa
				// aaabbbccc
				char c = cacheValue.charAt(idx);
				if (c == LIST_SEPARATOR) {
					// 标记分隔，进行处理
					isToken = !isToken;
				}

				if (isToken) {
					if (llen >= count) {
						recache += value + LIST_SEPARATOR;
					}
					llen++;
					// 重置
					value = StringUtils.EMPTY;
					isToken = !isToken;
				} else {
					value += c;
				}
				idx++;
				if (idx >= cacheValue.length() && StringUtils.isNotEmpty(value)) {
					if (llen >= count) {
						recache += value;
					}
					llen++;
					break;
				}
			}
			// 重置缓存
			put(key, recache);
		}
	}

	@Override
	public Object hget(Object key, String field) {
		try {
			if (key == null) {
				return null;
			} else {
				Element element = cache.get(getHKey(key, field));
				if (element != null) {
					if (log.isDebugEnabled()) {
						log.debug(String.format("hget: key=%s, vlaue=%s", getHKey(key, field), element.getObjectValue()));
					}
					return element.getObjectValue();
				}
			}
			return null;
		} catch (net.sf.ehcache.CacheException e) {
			throw new CacheException(e);
		}
	}

	@Override
	public void hput(Object key, String filed, Object value) {
		try {
			Element element = new Element(getHKey(key, filed), value);
			cache.put(element);
			if (log.isDebugEnabled()) {
				log.debug(String.format("hput: key=%s, vlaue=%s", getHKey(key, filed), value));
			}
		} catch (Exception e) {
			throw new CacheException(e);
		}
	}

	@Override
	public void hrem(Object key, String filed) {
		try {
			cache.remove(getHKey(key, filed));
			if (log.isDebugEnabled()) {
				log.debug(String.format("remove: key=%s", getHKey(key, filed)));
			}
		} catch (Exception e) {
			throw new CacheException(e);
		}
	}

	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	@Override
	public Object hexists(Object key, String field) {
		try {
			if (key == null) {
				return null;
			} else {
				Element element = cache.get(getHKey(key, field));
				if (element != null) {
					if (log.isDebugEnabled()) {
						log.debug(String.format("hget: key=%s, vlaue=%s", getHKey(key, field), element.getObjectValue()));
					}
					return Boolean.valueOf("true");
				} else {
					return Boolean.valueOf("false");
				}
			}
		} catch (net.sf.ehcache.CacheException e) {
			throw new CacheException(e);
		}
	}

}
