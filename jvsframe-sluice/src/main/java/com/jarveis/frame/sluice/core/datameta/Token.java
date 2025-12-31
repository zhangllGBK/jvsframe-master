package com.jarveis.frame.sluice.core.datameta;

import com.jarveis.frame.sluice.core.datameta.DataMeta.DataType;

/**
 * 表达式编译词元素
 *
 * @author liuguojun
 * @since  2018-07-25
 */
public class Token {

	// 词元的语法类型
	public enum TokenType {
		// 常量
		CONSTANT,
		// 变量
		VARIABLE,
		// 函数
		FUNCTION,
		// 分隔符
		SPLITOR
	}

	// Token的词元类型：常量，变量，函数，分割符
	private TokenType tokenType;
	// 当TokenType = CONSTANT 时,constant存储常量描述
	private Constant constant;
	// 当TokenType = VARIABLE 时,variable存储变量描述
	private Variable variable;
	// 存储字符描述
	private String tokenText;
	// 词元在表达式中的起始位置
	private int startPosition = -1;

	public static Token createConstantToken(DataType dataType, Object dataValue) {
		Token instance = new Token();
		instance.constant = new Constant(dataType, dataValue);
		instance.tokenType = TokenType.CONSTANT;
		if (dataValue != null) {
			instance.tokenText = instance.constant.getDataValueText();
		}
		return instance;
	}

	public static Token createConstantToken(Constant constant) throws Exception {
		if (constant == null) {
			throw new Exception("非法参数异常：常量为null");
		}
		Token instance = new Token();
		instance.constant = constant;
		instance.tokenType = TokenType.CONSTANT;
		if (constant.getDataValue() != null) {
			instance.tokenText = constant.getDataValueText();
		}
		return instance;
	}

	public static Token createVariableToken(String variableName) {
		Token instance = new Token();
		instance.variable = new Variable(variableName);
		instance.tokenType = TokenType.VARIABLE;
		instance.tokenText = variableName;
		return instance;
	}

	public static Token createReference(Reference ref) {
		Token instance = new Token();
		instance.constant = new Constant(ref);
		instance.tokenType = TokenType.CONSTANT;
		if (ref != null) {
			instance.tokenText = instance.constant.getDataValueText();
		}
		return instance;
	}

	public static Token createFunctionToken(String functionName)
			throws Exception {
		if (functionName == null) {
			throw new Exception("非法参数：函数名称为空");
		}
		Token instance = new Token();
		instance.tokenText = functionName;
		instance.tokenType = TokenType.FUNCTION;
		return instance;
	}

	public static Token createSplitorToken(String splitorText) throws Exception {
		if (splitorText == null) {
			throw new Exception("非法参数：分隔符为空");
		}
		Token instance = new Token();
		instance.tokenText = splitorText;
		instance.tokenType = TokenType.SPLITOR;
		return instance;
	}

	private Token() {
	}

	/**
	 * 获取Token的词元类型
	 * 
	 * @return
	 */
	public TokenType getTokenType() {
		return tokenType;
	}

	/**
	 * 获取Token的常量描述
	 * 
	 * @return
	 */
	public Constant getConstant() {
		return this.constant;
	}

	/**
	 * 获取Token的变量描述
	 * 
	 * @return
	 */
	public Variable getVariable() {
		return this.variable;
	}

	/**
	 * 获取Token的方法名类型值
	 * 
	 * @return
	 */
	public String getFunctionName() {
		return this.tokenText;
	}

	/**
	 * 获取Token的分隔符类型值
	 * 
	 * @return
	 */
	public String getSplitor() {
		return this.tokenText;
	}

	public int getStartPosition() {
		return startPosition;
	}

	public void setStartPosition(int startPosition) {
		this.startPosition = startPosition;
	}

	@Override
	public String toString() {
		return tokenText;
	}
}
