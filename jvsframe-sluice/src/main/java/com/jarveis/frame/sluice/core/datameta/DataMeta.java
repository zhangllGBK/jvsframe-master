package com.jarveis.frame.sluice.core.datameta;

/**
 * 基础数据类型描述
 *
 * @author liuguojun
 * @since  2018-07-25
 */
public abstract class DataMeta {

	// 数据类型
	public enum DataType {
		// NULL类型
		DATATYPE_NULL,
		// 字符窜
		DATATYPE_STRING,
		// 布尔类
		DATATYPE_BOOLEAN
	}

	// 数据类型
	DataType dataType;
	// 值
	Object dataValue;
	// 引用类型标识
	private boolean isReference;

	public DataMeta(DataType dataType, Object dataValue) {
		this.dataType = dataType;
		this.dataValue = dataValue;
	}

	public DataType getDataType() throws Exception {
		if (isReference) {
			return this.getReference().getDataType();
		} else {
			return dataType;
		}
	}

	public Object getDataValue() {
		return dataValue;
	}

	public String getDataValueText() {
		if (dataValue == null) {
			return null;
		} else {
			return dataValue.toString();
		}
	}

	/**
	 * 获取Token的字符窜类型值
	 * 
	 * @return
	 */
	public String getStringValue() {
		return getDataValueText();
	}

	/**
	 * 获取Token的boolean类型值
	 * 
	 * @return
	 * @throws Exception
	 */
	public Boolean getBooleanValue() throws Exception {
		if (DataType.DATATYPE_BOOLEAN != this.dataType) {
			throw new Exception("当前常量类型不支持此操作");
		}
		return (Boolean) dataValue;
	}

	/**
	 * 获取Token的引用对象
	 * 
	 * @return
	 * @throws Exception
	 */
	public Reference getReference() throws Exception {
		if (!this.isReference) {
			throw new Exception("当前常量类型不支持此操作");
		}
		return (Reference) dataValue;
	}

	@Override
	public boolean equals(Object o) {

		if (o == this) {
			return true;

		} else if (o instanceof DataMeta) {

			DataMeta bdo = (DataMeta) o;
			if (this.isReference() && bdo.isReference) {
				try {
					return this.getReference() == bdo.getReference();
				} catch (Exception e) {
					return false;
				}
			}

			if (bdo.dataType == dataType) {
				if (bdo.dataValue != null && bdo.dataValue.equals(dataValue)) {
					return true;
				} else if (bdo.dataValue == null && dataValue == null) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}

		} else {
			return false;
		}
	}

	public Object toJavaObject() throws Exception {
		if (null == this.dataValue) {
			return null;
		}

		if (DataMeta.DataType.DATATYPE_BOOLEAN == this.getDataType()) {
			return getBooleanValue();
		} else if (DataMeta.DataType.DATATYPE_STRING == this.getDataType()) {
			return getStringValue();
		} else {
			throw new Exception("映射Java类型失败：无法识别的数据类型");
		}
	}

	public boolean isReference() {
		return isReference;
	}

	void setReference(boolean isReference) {
		this.isReference = isReference;
	}

}
