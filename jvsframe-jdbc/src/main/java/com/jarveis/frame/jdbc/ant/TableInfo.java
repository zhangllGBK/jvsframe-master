package com.jarveis.frame.jdbc.ant;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.jarveis.frame.bean.BeanUtil;
import com.jarveis.frame.bean.ReflectionUtils;

/**
 * 类信息
 * 
 * @author liuguojun
 */
public class TableInfo {

	private String table; // 数据表名
	private List<ColumnInfo> columnInfos; // 类型的属性
	private List<ColumnInfo> primaryKeys; // 主键列
	private ShardAlgorithm shardAlgorithm; // 分表算法
	private String shardColumn; // 分表字段

	public TableInfo(Serializable s) {
		Class type;
		if (s instanceof Class) {
			type = (Class) s;
		} else {
			type = s.getClass();
		}

		Table table = (Table) type.getAnnotation(Table.class);
		if (table == null) {
			throw new RuntimeException("in the " + type.getName()
					+ ", not configured @Table Annotation");
		}
		if (!"".equals(table.name().trim())) {
			this.table = table.name();
		} else {
			this.table = type.getSimpleName().toLowerCase();
		}

		setColumnInfos(type);
		setShardStrategy(type);
	}

	/**
	 * 获取数据表名
	 * 
	 * @return String
	 */
	public String getTable() {
		return table;
	}

	/**
	 * 获取主键集
	 * 
	 * @return List<ColumnInfo>
	 */
	public List<ColumnInfo> getPrimaryKeys() {
		return primaryKeys;
	}

	/**
	 * 字段信息集
	 * 
	 * @return List<ColumnInfo>
	 */
	public List<ColumnInfo> getColumnInfos() {
		return columnInfos;
	}

	/**
	 * 获取字段信息
	 */
	private void setColumnInfos(Class type) {
		List<Field> fields = BeanUtil.getFields(type);
		
		if (columnInfos == null) {
			columnInfos = new ArrayList<ColumnInfo>(fields.size());
			primaryKeys = new ArrayList<ColumnInfo>(1);
		}

		for (int i = 0; i < fields.size(); i++) {
			Field field = fields.get(i);
			// 获取字段
			Column column = (Column) field.getAnnotation(Column.class);
			if (column == null) {
				continue;
			}
			ColumnInfo columnInfo = getFieldInfo(field, column);
			if (columnInfo.isPrimaryKey()) {
				primaryKeys.add(columnInfo);
			}
			columnInfos.add(columnInfo);
		}
	}

	/**
	 * 获取字段的属性信息
	 * 
	 * @param field
	 * @param column
	 * @return FieldInfo
	 */
	private ColumnInfo getFieldInfo(Field field, Column column) {
		ColumnInfo columnInfo = new ColumnInfo();

		columnInfo.setField(field.getName());
		columnInfo.setColumn(column.name());
		columnInfo.setPrimaryKey(column.primaryKey());

		return columnInfo;
	}

	/**
	 * 通过字段名称来获取字段值
	 * 
	 * @param fieldName
	 * @return Object
	 */
	public ColumnInfo getFieldInfo(String fieldName) {
		for (ColumnInfo columnInfo : columnInfos) {
			// 当子类中的字段名称等于超类中的字段名称
			if (columnInfo.getField().equals(fieldName)) {
				return columnInfo;
			}
		}
		return null;
	}
	
	/**
	 * 通过字段名称来获取字段值
	 * 
	 * @param columnName
	 * @return Object
	 */
	public ColumnInfo getColumnInfo(String columnName) {
		for (ColumnInfo columnInfo : columnInfos) {
			// 当子类中的字段名称等于超类中的字段名称
			if (columnInfo.getColumn().equals(columnName)) {
				return columnInfo;
			}
		}
		return null;
	}

	/**
	 * 设置分表策略
	 *
	 * @since 2024-08-06
	 * @param type
	 */
	private void setShardStrategy(Class type) {
		TableShardStrategy tableShardStrategy = (TableShardStrategy) type.getAnnotation(TableShardStrategy.class);
		if (tableShardStrategy != null) {
			Object algorithm = ReflectionUtils.newInstance(tableShardStrategy.algorithm());
			if (algorithm != null && algorithm instanceof ShardAlgorithm) {
				this.shardAlgorithm = (ShardAlgorithm) algorithm;
				this.shardColumn = tableShardStrategy.column();
			}
		}
	}
}
