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
public class MapUtil {

	/**
	 * 通过MapKey重置Map的键
	 * <pre>
	 * 数据源：
	 * Map map = new HashMap();
	 * map.put("1", "tom");
	 * map.put("2", "21");
	 * 
	 * MapKey的定义：
	 * class UserMapKey implements MapKey{
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
	 * Map resetMap = MapUtil.resetMapKey(map, UserMapKey);
	 * 
	 * 返回结果：
	 * resetMap = {
	 * 	"name": "tom",
	 * 	"age": "21"
	 * }
	 * </pre>
	 * 
	 * @param data 数据
	 * @param mapKey 数据键
	 * @return Map 转换后的数据对象
	 */
	public static Map<String, Object> resetMapKey(Map<String, Object> data,
			MapKey mapKey) {
		HashMap<String, Object> resetMap = new HashMap<>();
		
		List<Field> fields = BeanUtil.getFields(mapKey.getClass());
		for (Field field : fields) {
			String fieldName = field.getName();
			String key = (String)BeanUtil.getFieldValue(mapKey, fieldName);
			resetMap.put(fieldName, data.get(key));
		}

		return resetMap;
	}
	
	/**
	 * 通过MapKey重置Map的键
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
	 * class UserMapKey implements MapKey{
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
	 * List resetList = MapUtil.resetMapKey(list, UserMapKey);
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
	 * @param data 数据
	 * @param mapKey 数据键
	 * @return 转换后的数据对象
	 */
	public static List<Map<String, Object>> resetMapKey(List<Map<String, Object>> data, MapKey mapKey){
		List<Map<String, Object>> resetList = new ArrayList<>();
		
		for (Map<String, Object> item : data) {
			resetList.add(resetMapKey(item, mapKey));
		}
		
		return resetList;
	}
}
