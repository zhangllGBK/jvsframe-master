package com.jarveis.frame.sluice.core.datameta;

/**
 * 变量类型
 *
 * @author liuguojun
 * @since  2018-07-25
 */
public class Variable extends DataMeta {

	// 变量名
	private String variableName;

	/**
	 * 根据别名和参数值，构造 Variable 实例
	 * 
	 * @param variableName
	 * @param variableValue
	 * @return Variable
	 * @throws Exception
	 */
	public static Variable createVariable(String variableName, Object variableValue) {

		if (variableValue instanceof Boolean) {
			return new Variable(variableName, DataType.DATATYPE_BOOLEAN, variableValue);
		} else if (variableValue instanceof String) {
			return new Variable(variableName, DataType.DATATYPE_STRING, variableValue);
		} else {
			throw new RuntimeException(variableValue.getClass() + "无法识别的变量类型");
		}

	}

	public Variable(String variableName) {
		this(variableName, null, null);
	}

	public Variable(String variableName, DataType variableDataType, Object variableValue) {
		super(variableDataType, variableValue);

		if (variableName == null) {
			throw new RuntimeException("非法参数：变量名为空");
		}

		this.variableName = variableName;
	}

	/**
	 * 获取变量名称
	 * 
	 * @return
	 */
	public String getVariableName() {
		return variableName;
	}

	/**
	 * 设置变量值
	 * 
	 * @param variableValue
	 * @throws Exception
	 */
	public void setVariableValue(Object variableValue) {
		this.dataValue = variableValue;
	}

	/**
	 * 设置变量数据类型
	 * 
	 * @param dataType
	 * @throws Exception
	 */
	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		} else if (o instanceof Variable && super.equals(o)) {
			Variable var = (Variable) o;
			if (variableName != null && variableName.equals(var.variableName)) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

}
