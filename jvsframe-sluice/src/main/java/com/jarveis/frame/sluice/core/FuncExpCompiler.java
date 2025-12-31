package com.jarveis.frame.sluice.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.jarveis.frame.sluice.core.datameta.Constant;
import com.jarveis.frame.sluice.core.datameta.DataMeta;
import com.jarveis.frame.sluice.core.datameta.DataMeta.DataType;
import com.jarveis.frame.sluice.core.datameta.Token;
import com.jarveis.frame.sluice.core.datameta.Variable;
import com.jarveis.frame.sluice.core.function.FunctionExecution;

/**
 * 表达式编译器
 *
 * @author liuguojun
 * @since  2018-07-25
 */
public class FuncExpCompiler {

	/**
	 * 编译表达式
	 * 
	 * @param tokens
	 * @return
	 */
	public List<Token> compile(List<Token> tokens) throws Exception {

		if (tokens == null || tokens.isEmpty()) {
			throw new NullPointerException("无法编译表达式");
		}

		// 1.初始化逆波兰式队列和操作符栈
		List<Token> rpnExpList = new ArrayList<Token>();
		Stack<Token> opStack = new Stack<Token>();
		// 初始化检查栈
		Stack<Token> verifyStack = new Stack<Token>();

		// 2.出队列中从左向右依次便利token
		// 2-1. 声明一个存储函数词元的临时变量
		Token function = null;
		for (Token token : tokens) {

			if (Token.TokenType.CONSTANT == token.getTokenType()) {
				// 读入一个常量，压入逆波兰式队列
				rpnExpList.add(token);
				// 同时压入校验栈
				verifyStack.push(token);

			} else if (Token.TokenType.VARIABLE == token.getTokenType()) {
				// 验证变量声明
				Variable var = VariableContainer.getVariable(token
						.getVariable().getVariableName());
				if (var == null) {
					// 当变量没有定义时，视为null型
					token.getVariable().setDataType(DataType.DATATYPE_NULL);

				} else if (var.getDataType() == null) {
					throw new Exception("表达式不合法，变量\"" + token.toString()
							+ "\"缺少定义;");
				} else {
					// 设置Token中的变量类型定义
					token.getVariable().setDataType(var.getDataType());
				}

				// 读入一个变量，压入逆波兰式队列
				rpnExpList.add(token);
				// 同时压入校验栈
				verifyStack.push(token);

			} else if (Token.TokenType.FUNCTION == token.getTokenType()) {
				// 遇到函数名称，则使用临时变量暂存下来，等待(的来临
				function = token;

			} else if (Token.TokenType.SPLITOR == token.getTokenType()) {
				// 处理读入的“（”
				if ("(".equals(token.getSplitor())) {
					// 如果此时_function != null,说明是函数的左括号
					if (function != null) {
						// 向逆波兰式队列压入"("
						rpnExpList.add(token);
						// 向校验栈压入
						verifyStack.push(token);

						// 将"("及临时缓存的函数压入操作符栈,括号在前
						opStack.push(token);
						opStack.push(function);

						// 清空临时变量
						function = null;
					}

					// 处理读入的“）”
				} else if (")".equals(token.getSplitor())) {

					boolean doPop = true;

					while (doPop && !opStack.empty()) {
						// 从操作符栈顶弹出操作符或者函数，
						Token onTopOp = opStack.pop();
						if (Token.TokenType.FUNCTION == onTopOp.getTokenType()) {
							// 如果遇到函数，则说明")"是函数的右括号
							// 执行函数校验
							Token result = verifyFunction(onTopOp, verifyStack);
							// 把校验结果压入检验栈
							verifyStack.push(result);

							// 校验通过，添加")"到逆波兰式中
							rpnExpList.add(token);
							// 将函数加入逆波兰式
							rpnExpList.add(onTopOp);

						} else if ("(".equals(onTopOp.getSplitor())) {
							// 如果遇到"(", 则操作结束
							doPop = false;
						}
					}

					if (doPop && opStack.empty()) {
						throw new Exception("在读入\")\"时，操作栈中找不到对应的\"(\" ");
					}

					// 处理读入的“,”
				} else if (",".equals(token.getSplitor())) {
					// 依次弹出操作符栈中的所有操作符，压入逆波兰式队列，直到遇见函数词元
					boolean doPeek = true;

					while (!opStack.empty() && doPeek) {
						Token onTopOp = opStack.peek();

						if (Token.TokenType.FUNCTION == onTopOp.getTokenType()) {
							// 遇见函数词元,结束弹出
							doPeek = false;

						} else if (Token.TokenType.SPLITOR == onTopOp
								.getTokenType()
								&& "(".equals(onTopOp.getSplitor())) {
							// 在读入","时，操作符栈顶为"(",则报错
							throw new Exception(
									"在读入\",\"时，操作符栈顶为\"(\",,(函数丢失) 位置："
											+ onTopOp.getStartPosition());
						}
					}
					// 栈全部弹出，但没有遇见函数词元
					if (doPeek && opStack.empty()) {
						throw new Exception("在读入\",\"时，操作符栈弹空，没有找到相应的函数词元 ");
					}
				}
			}
		}

		// 将操作栈内剩余的操作符逐一弹出，并压入逆波兰式队列
		while (!opStack.empty()) {
			Token onTopOp = opStack.pop();

			if (Token.TokenType.FUNCTION == onTopOp.getTokenType()) {
				// 如果剩余是函数，则函数缺少右括号")"
				throw new Exception("函数" + onTopOp.getFunctionName()
						+ "缺少\")\"");
			} else if ("(".equals(onTopOp.getSplitor())) {
				// 剩下的就只有“(”了，则说明表达式的算式缺少右括号")"
				throw new Exception("左括号\"(\"缺少配套的右括号\")\"");
			}
		}

		// 表达式校验完成，这是校验栈内应该只有一个结果,否则视为表达式不完成
		if (verifyStack.size() != 1) {
			StringBuffer errorBuffer = new StringBuffer("\r\n");
			while (!verifyStack.empty()) {
				Token onTop = verifyStack.pop();
				errorBuffer.append("\t").append(onTop.toString())
						.append("\r\n");
			}
			throw new Exception("表达式不完整.\r\n 校验栈状态异常:" + errorBuffer);
		}

		return rpnExpList;
	}

	/**
	 * 验证函数
	 * 
	 * @param funtionToken
	 * @param verifyStack
	 * @return
	 */
	private Token verifyFunction(Token funtionToken, Stack<Token> verifyStack)
			throws Exception {

		if (!verifyStack.empty()) {

			boolean doPop = true;
			List<DataMeta> args = new ArrayList<DataMeta>();
			Token parameter = null;
			// 弹出函数的参数，直到遇到"("时终止
			while (doPop && !verifyStack.empty()) {
				parameter = verifyStack.pop();

				if (Token.TokenType.CONSTANT == parameter.getTokenType()) {
					// 常量
					args.add(parameter.getConstant());

				} else if (Token.TokenType.VARIABLE == parameter.getTokenType()) {
					args.add(parameter.getVariable());

				} else if ("(".equals(parameter.getSplitor())) {
					doPop = false;

				} else {
					// 没有找到应该存在的右括号
					throw new Exception("表达式不合法，函数\""
							+ funtionToken.getFunctionName() + "\"遇到非法参数"
							+ parameter.toString() + ";");
				}
			}

			if (doPop && verifyStack.empty()) {
				// 操作栈以空，没有找到函数的左括号（
				throw new Exception("表达式不合法，函数\""
						+ funtionToken.getFunctionName() + "\"缺少\"(\"；");
			}

			// 校验函数
			DataMeta[] arguments = new DataMeta[args.size()];
			arguments = args.toArray(arguments);
			Constant result = FunctionExecution.varify(
					funtionToken.getFunctionName(), arguments);
			return Token.createConstantToken(result);
		} else {
			// 没有找到应该存在的右括号
			throw new Exception("表达式不合法，函数\"" + funtionToken.getFunctionName()
					+ "\"不完整");
		}
	}

}
