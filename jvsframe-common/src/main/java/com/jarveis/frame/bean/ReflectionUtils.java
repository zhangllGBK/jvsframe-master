package com.jarveis.frame.bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * 反射工具类
 * 
 * @author liuguojun
 */
public class ReflectionUtils {

	private static final Logger log = LoggerFactory.getLogger(ReflectionUtils.class);

	/**
	 * 实例化对象
	 * 
	 * @param className 类名
	 * @return Object
	 */
	public static Object newInstance(final String className) {
		try {
			return newInstance(Class.forName(className));
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
		}
		return null;
	}

	/**
	 * 实例化对象
	 *
	 * @param clazz 类对象
	 * @return Object
	 */
	public static Object newInstance(Class clazz) {
		try {
			return clazz.getDeclaredConstructor().newInstance();
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
		}
		return null;
	}

	/**
	 * 执行对象中的方法
	 * 
	 * @param object 实例
	 * @param methodName 方法名
	 * @param parameterTypes 参数类型
	 * @param parameters 参数值
	 * @return Object
	 */
	public static Object invokeMethod(final Object object,
			final String methodName, final Class<?>[] parameterTypes,
			final Object[] parameters) {
		Method method = getDeclaredMethod(object, methodName, parameterTypes);
		if (method == null) {
			throw new IllegalArgumentException("Could not find method ["
					+ methodName + "] on target [" + object + "]");
		}

		method.setAccessible(true);

		try {
			return method.invoke(object, parameters);
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
		}
		return null;
	}

	/**
	 * 循环向上转型, 获取对象的DeclaredMethod.
	 * 
	 * 如向上转型到Object仍无法找到, 返回null.
	 *
	 * @param object 实例
	 * @param methodName 方法名
	 * @param parameterTypes 参数类型
	 * @return Method
	 */
	protected static Method getDeclaredMethod(Object object, String methodName,
			Class<?>[] parameterTypes) {
		for (Class<?> superClass = object.getClass(); superClass != Object.class; superClass = superClass
				.getSuperclass()) {
			try {
				return superClass.getDeclaredMethod(methodName, parameterTypes);
			} catch (NoSuchMethodException ex) {
				log.error(ex.getMessage(), ex);
			}
		}
		return null;
	}

}
