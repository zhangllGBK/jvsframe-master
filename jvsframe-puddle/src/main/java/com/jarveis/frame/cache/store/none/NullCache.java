package com.jarveis.frame.cache.store.none;

import java.util.List;

import com.jarveis.frame.cache.Cache;

/**
 * @desc 空的缓存
 * @author liuguojun
 * @date 2018-08-31
 */
public class NullCache implements Cache {

	public void put(Object key, Object value) {
	}

	public void put(Object key, Object value, Integer expireInSec) {
	}

	public Object get(Object key) {
		return null;
	}

	public void remove(Object key) {
	}

	@Override
	public Object exists(Object key) {
		return null;
	}

	public void removeAll(List keys) {
	}

	public void clear() {
	}

	public void destroy() {
	}

	@Override
	public void lpush(Object key, Object value) {

	}

	@Override
	public void rpush(Object key, Object value) {

	}

	@Override
	public Object lpop(Object key) {
		return null;
	}

	@Override
	public Object rpop(Object key) {
		return null;
	}

	@Override
	public Object llen(Object key) {
		return null;
	}

	@Override
	public Object lrange(Object key, int count) {
		return null;
	}

	@Override
	public void lrem(Object key, int count) {

	}

	@Override
	public Object hget(Object key, String field) {
		return null;
	}

	@Override
	public void hput(Object key, String filed, Object value) {

	}

	@Override
	public void hrem(Object key, String filed) {

	}

	@Override
	public Object hexists(Object key, String field) {
		return null;
	}

}
