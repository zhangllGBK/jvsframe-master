package com.jarveis.frame.bean;

import com.jarveis.frame.util.CharacterUtil;
import com.jarveis.frame.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * class工具类
 * 
 * @author liuguojun
 */
public class BeanUtil {

	private static final Logger log = LoggerFactory.getLogger(BeanUtil.class);

	/**
	 * 跟据类型来创建对象
	 * 
	 * @param type
	 * @return Object
	 */
	public static Object newInstance(Class type) {
		return newInstance(type.getName());
	}

	/**
	 * 跟据类名来创建对象
	 * 
	 * @param className
	 * @return Object
	 */
	public static Object newInstance(String className) {
		Object object = null;
		try {
			object = Class.forName(className).newInstance();
		} catch (InstantiationException ex) {
			log.error(ex.getMessage(), ex);
		} catch (IllegalAccessException ex) {
			log.error(ex.getMessage(), ex);
		} catch (ClassNotFoundException ex) {
			log.error(ex.getMessage(), ex);
		}
		return object;
	}

	/**
	 * 获取可续列化对象的全称
	 * 
	 * <pre>
	 * [代码]
	 * String fn1 = BeanUtil.getFname(new User());
	 * String fn2 = BeanUtil.getFname(User.class);
	 * 
	 * [结果]
	 * fn1 = "com.jarveis.bean.User"
	 * fn2 = "com.jarveis.bean.User"
	 * </pre>
	 * 
	 * @param s
	 *            可续列化对象
	 * @return String
	 */
	public static String getFname(Serializable s) {
		if (s instanceof Class) {
			return ((Class) s).getName();
		} else {
			return s.getClass().getName();
		}
	}

	/**
	 * 获取类的简称
	 * 
	 * <pre>
	 * [代码]
	 * String sn = BeanUtil.getSname(User.class);
	 * 
	 * [结果]
	 * sn = "User"
	 * </pre>
	 * 
	 * @param type
	 *            Bean对象的Class对象
	 * @return String Bean对象的类名
	 */
	public static String getSname(Class type) {
		return type.getSimpleName();
	}

	/**
	 * 获取可续列化对象的简称
	 * 
	 * <pre>
	 * [代码]
	 * String sn = BeanUtil.getSname(new User());
	 * 
	 * [结果]
	 * sn = "User"
	 * </pre>
	 * 
	 * @param s
	 *            可续列化对象
	 * @return String
	 */
	public static String getSname(Serializable s) {
		return getSname(s.getClass());
	}

	/**
	 * 获取对象的属性
	 * 
	 * @param s
	 *            可续列化对象
	 * @param fn
	 *            属性名称
	 * @return Field
	 */
	protected static Field getDeclaredField(Serializable s, String fn) {
		return getDeclaredField(s.getClass(), fn);
	}

	/**
	 * 获取类的属性
	 * 
	 * @param type
	 * @param fn
	 * @return Field
	 */
	public static Field getDeclaredField(Class<?> type, String fn) {
		for (Class<?> superClass = type; !superClass.equals(Object.class); superClass = superClass
				.getSuperclass()) {
			try {
				return superClass.getDeclaredField(fn);
			} catch (NoSuchFieldException ex) {

			}
		}
		return null;
	}

	/**
	 * 强制转换属性为可访问
	 * 
	 * @param field
	 */
	protected static void makeAccessible(Field field) {
		if (!Modifier.isPublic(field.getModifiers())
				|| !Modifier.isPublic(field.getDeclaringClass().getModifiers())) {
			field.setAccessible(true);
		}
	}

	/**
	 * 获取Bean对象中的属性，包括继承的属性
	 * 
	 * <pre>
	 * [代码]
	 * User user = new User();
	 * List fields = BeanUtil.getFields(User.class);
	 * 
	 * [结果]
	 * fields = ["id", "name", "pwd"];
	 * </pre>
	 * 
	 * @param type
	 *            Class对象
	 * @return List
	 */
	public static List<Field> getFields(Class<?> type) {
		List<Field> fieldList = new ArrayList<Field>();

		// 查找当前类中的属性
		Field[] fields = type.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			// 过滤掉静态类型的属性
			int modifiers = fields[i].getModifiers();
			if (modifiers >= Modifier.STATIC) {
				continue;
			}
			// 过滤掉集合类型的属性
			if (fields[i].getType().equals(java.util.List.class)) {
				continue;
			}
			fieldList.add(fields[i]);
		}

		// 查找超类中的属性
		Class<?> superType = type.getSuperclass();
		if (!superType.equals(java.lang.Object.class)) {
			List<Field> superList = getFields(superType);
			// 过滤掉超类和子类中的重复元素
			for (int i = 0; i < fieldList.size(); i++) {
				Field subField = fieldList.get(i);
				String subFieldName = subField.getName();
				for (int j = 0; j < superList.size(); j++) {
					Field superField = (Field) superList.get(j);
					String superFieldName = superField.getName();
					// 当子类中的字段名称等于超类中的字段名称
					if (subFieldName.equals(superFieldName)) {
						superList.remove(superField);
						continue;
					}
				}
			}
			fieldList.addAll(superList);
		}

		return fieldList;
	}

	/**
	 * 获取Bean对象的属性值
	 * 
	 * <pre>
	 * [代码]
	 * User user = new User();
	 * user.setName("tom");
	 * String name = (String)BeanUtil.getFieldValue(user, "name");
	 * 
	 * [结果]
	 * name = "tom"
	 * </pre>
	 * 
	 * @param s
	 *            可续列化对象
	 * @param fn
	 *            属性名称
	 * @return Object
	 */
	public static Object getFieldValue(Serializable s, String fn) {
		return getFieldValue(s, getDeclaredField(s, fn));
	}

	/**
	 * 获取Bean对象的属性值
	 * 
	 * <pre>
	 * [代码]
	 * User user = new User();
	 * user.setName("tom");
	 * Field field = BeanUtil.getDeclaredField(user, "name");
	 * String name = (String)BeanUtil.getFiledValue(user, field);
	 * 
	 * [结果]
	 * name = "tom"
	 * </pre>
	 * 
	 * @param s
	 *            可续列化对象
	 * @param field
	 *            属性对象
	 * @return Object
	 */
	public static Object getFieldValue(Serializable s, Field field) {
		Object result = null;

		if (field != null) {
			makeAccessible(field);
			try {
				result = field.get(s);
			} catch (IllegalAccessException ex) {
				log.error(ex.getMessage(), ex);
			}
		}

		return result;
	}

	/**
	 * 设置Bean对象的属性值
	 * 
	 * <pre>
	 * [代码]
	 * User user = new User();
	 * BeanUtil.setFieldValue(user, "name", "tom");
	 * 
	 * [结果]
	 * user = {
	 *     name: "tom"
	 * };
	 * </pre>
	 * 
	 * @param s
	 *            可续列化对象
	 * @param fieldName
	 *            属性名称
	 * @param value
	 *            值
	 */
	public static void setFieldValue(Serializable s, String fieldName,
			Object value) {
		try {
			setFieldValue(s, getDeclaredField(s, fieldName), value);
		} catch (Exception ex) {
			log.error(s.getClass().toString() + "@" + fieldName
					+ " not found");
		}
	}

	/**
	 * 设置Bean对象的属性值
	 * 
	 * <pre>
	 * [代码]
	 * User user = new User();
	 * Field field = BeanUtil.getDeclaredField(user, "name");
	 * BeanUtil.setFieldValue(user, field, "tom");
	 * 
	 * [结果]
	 * user = {
	 *     name: "tom"
	 * };
	 * </pre>
	 * 
	 * @param s
	 *            可续列化对象
	 * @param field
	 *            属性对象
	 * @param value
	 *            值
	 * @throws Exception
	 */
	public static void setFieldValue(Serializable s, Field field, Object value)
			throws Exception {
		if (field != null) {
			makeAccessible(field);
			try {
				Class<?> fieldType = field.getType();
				if (value != null) {
					// 数据类型兼容处理
					if (fieldType.equals(Integer.class) || fieldType.equals(int.class)) {
						if (value instanceof String) {
							field.set(s, Integer.valueOf((String)value));
						} else {
							field.set(s, value);
						}
					} else if (fieldType.equals(Long.class) || fieldType.equals(long.class)) {
						if (value instanceof String) {
							field.set(s, Long.valueOf((String)value));
						} else {
							field.set(s, value);
						}
					} else if (fieldType.equals(Float.class) || fieldType.equals(float.class)) {
						if (value instanceof String) {
							field.set(s, Float.valueOf((String)value));
						} else if (value instanceof BigDecimal) {
							field.set(s, ((BigDecimal) value).floatValue());
						} else {
							field.set(s, value);
						}
					} else if (fieldType.equals(Double.class) || fieldType.equals(double.class)) {
						if (value instanceof String) {
							field.set(s, Double.valueOf((String)value));
						} else if (value instanceof BigDecimal) {
							field.set(s, ((BigDecimal) value).doubleValue());
						} else {
							field.set(s, value);
						}
					} else if (fieldType.equals(Boolean.class) || fieldType.equals(boolean.class)) {
						if (value instanceof String) {
							field.set(s, Boolean.valueOf((String)value));
						} else {
							field.set(s, value);
						}
					} else if (fieldType.equals(String.class)) {
						if (value instanceof String) {
							field.set(s, value);
						} else {
							field.set(s, String.valueOf(value));
						}
					} else if (fieldType.equals(Date.class)) {
						if (value instanceof String) {
							field.set(s, DateUtil.getDate((String)value));
						} else if (value instanceof Long) {
							field.set(s, DateUtil.getDate((Long)value));
						} else {
							field.set(s, value);
						}
					} else if (fieldType.equals(BigDecimal.class)) {
						if (value instanceof String) {
							field.set(s, new BigDecimal((String) value));
						} else if (value instanceof Long) {
							field.set(s, BigDecimal.valueOf((Long)value));
						} else if (value instanceof Double) {
							field.set(s, BigDecimal.valueOf((Double)value));
						} else {
							field.set(s, value);
						}
					}
				} else {
					field.set(s, value);
				}
			} catch (IllegalAccessException ex) {
				log.error(ex.getMessage(), ex);
			}
		} else {
			throw new Exception("filed is not found");
		}
	}

	/**
	 * 连接List中Bean对象属性的值
	 * 
	 * <pre>
	 * [代码]
	 * User user1 = new User();
	 * user1.setId("0001");
	 * 
	 * User user2 = new User();
	 * user2.setId("0002");
	 * 
	 * List list = new ArrayList();
	 * list.add(user1);
	 * list.add(user2);
	 * 
	 * Stirng ids = BeanUtil.joinBeansId(list, "id");
	 * 
	 * [结果]
	 * ids = "0001,0002"
	 * </pre>
	 * 
	 * @param list
	 *            Bean对象的集合
	 * @return String
	 */
	public static String joinBeansProperty(List<Serializable> list, String fn) {
		StringBuffer ids = new StringBuffer();

		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				Serializable s = (Serializable) list.get(i);
				ids.append((String) BeanUtil.getFieldValue(s, fn));
				if (i + 1 != list.size()) {
					ids.append(CharacterUtil.SEPARATOR);
				}
			}
		}

		return ids.toString();
	}

	/**
	 * 连接List中Bean对象Id的值
	 * 
	 * <pre>
	 * [代码]
	 * User user1 = new User();
	 * user1.setId("0001");
	 * 
	 * User user2 = new User();
	 * user2.setId("0002");
	 * 
	 * List list = new ArrayList();
	 * list.add(user1);
	 * list.add(user2);
	 * 
	 * Stirng ids = BeanUtil.joinBeansId(list);
	 * 
	 * [结果]
	 * ids = "0001,0002"
	 * </pre>
	 * 
	 * @param list
	 *            Bean对象的集合
	 * @return String
	 */
	public static String joinBeansId(List<Serializable> list) {
		return joinBeansProperty(list, "id");
	}
}
