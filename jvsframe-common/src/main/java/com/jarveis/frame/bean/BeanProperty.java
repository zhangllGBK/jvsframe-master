package com.jarveis.frame.bean;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Bean属性帮助类
 * 
 * @author liuguojun
 */
public class BeanProperty {

	/**
	 * 复制源对象(Bean)属性的值到目标对象(Bean)的属性中
	 * 
	 * <pre>
	 * [代码]
	 * User user1 = new User();
	 * user1.setId("0001")
	 * user1.setName("tom");
	 * 
	 * User user2 = new user();
	 * BeanProperty.copyProperties(user1, user2);
	 * 
	 * [结果]
	 * user2 = {
	 *     id: "0001",
	 *     name: "tom"
	 * }
	 * </pre>
	 * 
	 * @param source
	 *            源对象
	 * @param target
	 *            目标对象
	 */
	public static void copyProperties(Serializable source, Serializable target) {
		List<Field> srcFields = BeanUtil.getFields(source.getClass());
		List<Field> trgFields = BeanUtil.getFields(target.getClass());

		for (int i = 0; i < srcFields.size(); i++) {
			Field srcField = srcFields.get(i);
			String sfn = srcField.getName();
			for (int j = 0; j < trgFields.size(); j++) {
				Field trgField = trgFields.get(j);
				String tfn = trgField.getName();
				if (!sfn.equals(tfn)) {
					continue;
				}
				Object value = BeanUtil.getFieldValue(source, srcField);
				if (value != null) {
					try {
						BeanUtil.setFieldValue(target, trgField, value);
					} catch (Exception ex) {
					}
				}
			}
		}
	}

	/**
	 * 复制源对象(Map)键的值到目标对象(Bean)的属性中
	 * 
	 * <pre>
	 * [代码]
	 * Map map = new HashMap();
	 * user.put("id", "0001");
	 * user.put("name", "tom");
	 * 
	 * User user = new user();
	 * BeanProperty.copyProperties(map, user);
	 * 
	 * [结果]
	 * user = {
	 *     id: "0001",
	 *     name: "tom"
	 * }
	 * </pre>
	 * 
	 * @param source
	 *            源对象
	 * @param target
	 *            目标对象
	 * @throws Exception
	 */
	public static void copyProperties(Map<String, Object> source, Serializable target) throws Exception {
		List<Field> trgFields = BeanUtil.getFields(target.getClass());

		for (int i = 0; i < trgFields.size(); i++) {
			Field trgField = trgFields.get(i);
			String fieldName = trgField.getName();
			Class<?> fieldType = trgField.getType();
			Object value = source.get(fieldName);
			if (value != null) {
				BeanUtil.setFieldValue(target, trgField, value);
			}
		}
	}

	/**
	 * 复制配置属性对象中的值到对象中
	 * 
	 * @param prop
	 * @param target
	 * @throws Exception
	 */
	public static void copyProperties(Properties prop, Serializable target)
			throws Exception {
		Method[] methods = target.getClass().getDeclaredMethods();
		Iterator it = prop.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String key = (String) entry.getKey();
			String value = (String) entry.getValue();
			if (value == null) {
				continue;
			}
			StringBuffer mname = new StringBuffer("set");
			mname.append(key.substring(0, 1).toUpperCase());
			mname.append(key.substring(1));
			String methodName = mname.toString();
			int i = 0;
			for (; i < methods.length; i++) {
				if (methods[i].getName().equals(methodName)
						&& methods[i].getParameterTypes().length == 1) {
					break;
				}
			}

			Class type = methods[i].getParameterTypes()[0];
			if (int.class.equals(type)) {
				methods[i].invoke(target, Integer.parseInt(value));
			} else if (boolean.class.equals(type)) {
				methods[i].invoke(target, Boolean.parseBoolean(value));
			} else {
				methods[i].invoke(target, value);
			}
		}
	}

	/**
	 * 将bean对象转化为map对象
	 * 
	 * <pre>
	 * [代码]
	 * User user1 = new User();
	 * user1.setId("0001")
	 * user1.setName("tom");
	 * 
	 * Map user2 = BeanProperty.toMap(user1);
	 * 
	 * [结果]
	 * user2 = {
	 *     id: "0001",
	 *     name: "tom"
	 * }
	 * </pre>
	 * 
	 * @param source
	 * @return Map
	 * @throws Exception
	 */
	public static Map toMap(Serializable source) {
		List<Field> fields = BeanUtil.getFields(source.getClass());
		HashMap map = new HashMap(fields.size());

		for (int i = 0; i < fields.size(); i++) {
			Field field = fields.get(i);
			String key = field.getName();
			Object value = BeanUtil.getFieldValue(source, field);
			if (value != null) {
				map.put(key, value);
			}
		}

		return map;
	}

	/**
	 * 清理对象的属性值
	 * 
	 * <pre>
	 * [代码]
	 * User user = new User();
	 * user.setId("0001")
	 * user.setName("tom");
	 * 
	 * BeanProperty.cleanProperties(user);
	 * 
	 * [结果]
	 * user = {
	 *     id: "",
	 *     name: ""
	 * }
	 * </pre>
	 * 
	 * @param s
	 *            可续列化对象
	 */
	public static void cleanProperties(Serializable s) {
		List<Field> fields = BeanUtil.getFields(s.getClass());
		for (int i = 0; i < fields.size(); i++) {
			String fieldName = fields.get(i).getName();
			BeanUtil.setFieldValue(s, fieldName, null);
		}
	}
}
