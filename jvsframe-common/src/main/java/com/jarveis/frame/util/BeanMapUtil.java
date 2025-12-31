package com.jarveis.frame.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jarveis.frame.bean.BeanUtil;

/**
 * Map工具类
 * 
 * @author liuguojun
 * 
 */
public class BeanMapUtil {

	/**
	 * 通过BeanMap重置Map的键
	 * 
	 * <pre>
	 * 数据源：
	 * Map map = new HashMap();
	 * map.put("1", "tom");
	 * map.put("2", "21");
	 * 
	 * MapKey的定义：
	 * class User implements BeanMap{
	 * 	private String name = "1";
	 * 	private String age = "2";
	 * 
	 * 	public String getName(){
	 * 		return name;
	 * 	}
	 * 	public String getAge(){
	 * 		return age;
	 * 	}
	 * }
	 * 
	 * 重置Map的键：
	 * Map resetMap = MapUtil.resetMapKey(map, User);
	 * 
	 * 返回结果：
	 * resetMap = {
	 * 	"name": "tom",
	 * 	"age": "21"
	 * }
	 * </pre>
	 * 
	 * @param data
	 * @param beanMap
	 * @return Map
	 */
	public static Map<String, Object> resetMapKey(Map<String, Object> data,
			BeanMap beanMap) {
		HashMap<String, Object> resetMap = new HashMap<String, Object>();

		List<Field> fields = BeanUtil.getFields(beanMap.getClass());
		for (Field field : fields) {
			String fieldName = field.getName();
			String key = (String) BeanUtil.getFieldValue(beanMap, fieldName);
			resetMap.put(fieldName, data.get(key));
		}

		return resetMap;
	}

	/**
	 * 通过BeanMap重置Map的键
	 * 
	 * <pre>
	 * 数据源：
	 * List list = new ArrayList();
	 * Map map1 = new HashMap();
	 * map1.put("1", "tom");
	 * map1.put("2", "21");
	 * list.add(map1)
	 * 
	 * Map map2 = new HashMap();
	 * map2.put("1", "jacy");
	 * map2.put("2", "22");
	 * list.add(map2)
	 * 
	 * MapKey的定义：
	 * class User implements BeanMap{
	 * 	private String name = "1";
	 * 	private String age = "2";
	 * 
	 * 	public String getName(){
	 * 		return name;
	 * 	}
	 * 	public String getAge(){
	 * 		return age;
	 * 	}
	 * }
	 * 
	 * 重置Map的键：
	 * List resetList = MapUtil.resetMapKey(list, User);
	 * 
	 * 返回结果：
	 * resetList = [
	 * {
	 * 	"name": "tom",
	 * 	"age": "21"
	 * },
	 * {
	 * 	"name": "jack",
	 * 	"age": "22"
	 * }
	 * ]
	 * </pre>
	 * 
	 * @param data
	 * @param beanMap
	 * @return Map
	 */
	public static List<Map<String, Object>> resetMapKey(
			List<Map<String, Object>> data, BeanMap beanMap) {
		List<Map<String, Object>> resetList = new ArrayList<Map<String, Object>>();

		for (Map<String, Object> item : resetList) {
			resetList.add(resetMapKey(item, beanMap));
		}

		return resetList;
	}

	/**
	 * 转化MapKey对象，为Map集合
	 * 
	 * <pre>
	 * BeanMap的定义：
	 * class User implements BeanMap{
	 * 	private String name = "tom";
	 * 	private String age = "21";
	 * 
	 * 	public String getName(){
	 * 		return name;
	 * 	}
	 * 	public String getAge(){
	 * 		return age;
	 * 	}
	 * }
	 * 
	 * 数据转换：
	 * User user = new User();
	 * Map map = MapUtil.toMap(user);
	 * 
	 * 返回结果：
	 * map = {
	 * 	"name": "tom",
	 * 	"age": "21"
	 * }
	 * </pre>
	 * 
	 * @param beanMap
	 * @return
	 */
	public static Map<String, Object> toMap(BeanMap beanMap) {
		HashMap<String, Object> resetMap = new HashMap<String, Object>();

		List<Field> fields = BeanUtil.getFields(beanMap.getClass());
		for (Field field : fields) {
			String fieldName = field.getName();
			Object fieldValue = BeanUtil.getFieldValue(beanMap, fieldName);
			resetMap.put(fieldName, fieldValue);
		}

		return resetMap;
	}

	/**
	 * 转化Map集合，为BeanMap对象
	 * 
	 * <pre>
	 * BeanMap的定义：
	 * class User implements BeanMap{
	 * 	private String name = "";
	 * 	private String age = "";
	 * 
	 * 	public String getName(){
	 * 		return name;
	 * 	}
	 * 	public String getAge(){
	 * 		return age;
	 * 	}
	 * }
	 * 
	 * Map集合的定义
	 * map = {
	 * 	"name": "tom",
	 * 	"age": "21"
	 * }
	 * 
	 * 数据转换：
	 * User user = (User)MapUtil.toBean(map, User.class);
	 * 
	 * 返回结果：
	 * user = {
	 * 	"name": "tom",
	 * 	"age": "21"
	 * }
	 * </pre>
	 * 
	 * @param map
	 * @param clazz
	 * @return
	 */
	public static BeanMap toBean(Map<String, Object> map, Class clazz) {
		BeanMap mapKey = null;
		
		Object obj = BeanUtil.newInstance(clazz);
		if (obj instanceof BeanMap) {
			mapKey = (BeanMap)obj;
			List<Field> fields = BeanUtil.getFields(clazz);
			for (Field field : fields) {
				String fieldName = field.getName();
				Object fieldValue = map.get(fieldName);
				BeanUtil.setFieldValue(mapKey, fieldName, fieldValue);
			}
		}

		return mapKey;
	}
}
