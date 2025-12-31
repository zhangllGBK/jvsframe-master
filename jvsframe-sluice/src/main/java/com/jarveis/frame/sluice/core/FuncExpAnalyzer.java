package com.jarveis.frame.sluice.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.jarveis.frame.sluice.core.datameta.DataMeta.DataType;
import com.jarveis.frame.sluice.core.datameta.Element;
import com.jarveis.frame.sluice.core.datameta.Element.ElementType;
import com.jarveis.frame.sluice.core.datameta.Token;

/**
 * 表达式分析器
 *
 * @author liuguojun
 * @since  2018-07-25
 */
public class FuncExpAnalyzer {

	private Stack<String> parenthesis = new Stack<String>();// 匹配圆括号的栈

	/**
	 * 分析表达式，得到编译所需的词元素
	 * <pre>
	 *     String str = "$match(userids, usercode)";
	 *     List list = funcExpAnalyzer.analyze(str);
	 *     // [1, +, 2]
	 * </pre>
	 * 
	 * @param expression
	 * @return
	 * @throws Exception
	 */
	public List<Token> analyze(String expression) throws Exception {
		AnalyzeReader reader = new AnalyzeReader(expression);
		List<Token> list = new ArrayList<Token>();
		Token expressionToken = null;
		Element ele = null;
		try {
			while ((ele = reader.readToken()) != null) {
				expressionToken = changeToToken(ele);
				// 如果是括号，则记录下来，最后进行最后进行匹配
				pushParenthesis(ele);
				list.add(expressionToken);
			}
		} catch (Exception e) {
			throw new Exception("表达式词元格式异常");
		} finally {
			reader.close();
		}

		if (!parenthesis.isEmpty()) {
			throw new Exception("括号匹配出错");
		}

		return list;
	}

	/**
	 * 验证括号是否配对
	 * 
	 * @param ele
	 * @throws Exception
	 */
	private void pushParenthesis(Element ele) throws Exception {
		if (ElementType.SPLITOR == ele.getType()) {
			if (ele.getText().equals("(")) {
				parenthesis.push("(");
			} else if (ele.getText().equals(")")) {
				if (parenthesis.isEmpty() || !parenthesis.peek().equals("(")) {
					throw new Exception("括号匹配出错");
				} else {
					parenthesis.pop();
				}
			}
		}
	}

	/**
	 * 将分析的词元素转换成编译的词元素
	 * 
	 * @param ele
	 * @return
	 * @throws Exception
	 */
	private Token changeToToken(Element ele) throws Exception {
		if (ele == null) {
			throw new Exception("分析词元数据不能为空");
		}
		Token token = null;

		if (ElementType.STRING == ele.getType()) {
			token = Token.createConstantToken(DataType.DATATYPE_STRING,
					ele.getText());
		} else if (ElementType.VARIABLE == ele.getType()) {
			token = Token.createVariableToken(ele.getText());
		} else if (ElementType.FUNCTION == ele.getType()) {
			token = Token.createFunctionToken(ele.getText());
		} else if (ElementType.SPLITOR == ele.getType()) {
			token = Token.createSplitorToken(ele.getText());
		} else {
			throw new Exception("分析词元的数据类型有误");
		}
		token.setStartPosition(ele.getIndex());

		return token;
	}
	
}
