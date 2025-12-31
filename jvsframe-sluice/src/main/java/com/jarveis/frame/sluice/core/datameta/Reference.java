package com.jarveis.frame.sluice.core.datameta;

import com.jarveis.frame.sluice.core.datameta.DataMeta.DataType;
import com.jarveis.frame.sluice.core.datameta.Token.TokenType;
import com.jarveis.frame.sluice.core.function.FunctionExecution;

/**
 * 引用类型
 *
 * @author liuguojun
 * @since  2018-07-25
 */
public class Reference {

	private Token token;

	private Constant[] arguments;
	// 引用对象实际的数据类型
	private DataType dataType;

	public Reference(Token token, Constant[] args) throws Exception {
		this.token = token;
		this.arguments = args;
		// 记录Reference实际的数据类型
		if (Token.TokenType.FUNCTION == token.getTokenType()) {
			Constant result = FunctionExecution.varify(token.getFunctionName(), args);
			dataType = result.getDataType();
		} else {
			throw new Exception(token.getTokenType() + ",不是Reference类型");
		}
	}

	public DataType getDataType() {
		return dataType;
	}

	public Constant[] getArgs() {
		return arguments;
	}

	public void setArgs(Constant[] args) {
		this.arguments = args;
	}

	public Token getToken() {
		return token;
	}

	public void setToken(Token token) {
		this.token = token;
	}

	/**
	 * 执行引用对象指待的表达式
	 * 
	 * @return
	 */
	public Constant execute() throws Exception {

		if (TokenType.FUNCTION == token.getTokenType()) {
			// 执行函数
			return FunctionExecution.execute(token.getFunctionName(), arguments);

		} else {
			throw new Exception("不支持的Reference执行异常");
		}
	}

}
