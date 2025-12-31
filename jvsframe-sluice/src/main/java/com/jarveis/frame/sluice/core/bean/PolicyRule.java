package com.jarveis.frame.sluice.core.bean;

import com.jarveis.frame.security.MD5Cipher;
import com.jarveis.frame.sluice.SluiceCache;
import com.jarveis.frame.sluice.core.FuncExpAnalyzer;
import com.jarveis.frame.sluice.core.FuncExpCompiler;
import com.jarveis.frame.sluice.core.PreparedFuncExp;
import com.jarveis.frame.sluice.core.VariableContainer;
import com.jarveis.frame.sluice.core.datameta.Token;
import com.jarveis.frame.util.CharacterUtil;
import com.jarveis.frame.util.Param;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 功能规则
 *
 * @author liuguojun
 * @since  2018-07-24
 */
public class PolicyRule implements Serializable {

	private static final Logger log = LoggerFactory.getLogger(PolicyRule.class);

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	private String funcId; // 服务编号
	private String rank; // 规则类型（before | after）
	private String exp; // 规则表达式
	private int errcode; // 通过规则返回的errcode
	private String errdesc; // 错误描述
	private int priority; // 规则优先级，越大优先级越高
	private long lasttime = 0L; // 更新时间（与sluice配置文件的更新时间一致）
	private PreparedFuncExp preparedFuncExp;
	private List<String> variables;

	public PolicyRule() {
		super();
	}

	public String getId(){
		return MD5Cipher.encrypt(funcId + rank + exp + errcode);
	}

	@Override
	public boolean equals(Object obj) {
		PolicyRule policyRule = (PolicyRule) obj;
		return rank.equals(policyRule.getRank()) && exp.equals(policyRule.getExp()) && errcode == policyRule.getErrcode() && priority == policyRule.getPriority();
	}

	@Override
	public String toString() {
		return "PolicyRule [funcId=" + funcId + ", rank=" + rank + ", exp=" + exp + ", errcode=" + errcode + ", priority=" + priority + "]";
	}

	/**
	 * 获取服务编号
	 *
	 * @return
	 */
	public String getFuncId() {
		return funcId;
	}

	/**
	 * 设置服务编号
	 *
	 * @param funcId
	 * @since 2020-06-16
	 */
	public void setFuncId(String funcId) {
		this.funcId = funcId;
	}

	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}

	public int getErrcode() {
		return errcode;
	}

	public void setErrcode(int errcode) {
		this.errcode = errcode;
	}

	public String getErrdesc() {
		return errdesc;
	}

	public void setErrdesc(String errdesc) {
		this.errdesc = errdesc;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public int getPriority() {
		return priority;
	}

	public String getExp() {
		return exp;
	}

	/**
	 * 设置规则
	 * 
	 * @param expression
	 */
	public void setExp(String expression) {
		this.exp = expression;
		FuncExpAnalyzer analyzer = new FuncExpAnalyzer();
		FuncExpCompiler compiler = new FuncExpCompiler();
		// 解析表达式词元
		this.variables = new ArrayList<String>();
		// 生成预编译表达式
		this.preparedFuncExp = preparedCompile(analyzer, compiler, this.exp);
	}

	/**
	 * 返回更新时间
	 *
	 * @return
	 */
	public long getLasttime() {
		return lasttime;
	}

	/**
	 * 设置更新时间
	 *
	 * @param lasttime
	 */
	public void setLasttime(long lasttime) {
		this.lasttime = lasttime;
	}

	/**
	 * 预编译表达式
	 * 
	 * @param analyzer
	 * @param compiler
	 * @param expression
	 * @return
	 */
	private PreparedFuncExp preparedCompile(FuncExpAnalyzer analyzer, FuncExpCompiler compiler, String expression) {
		if (StringUtils.isEmpty(expression)) {
			return null;
		}

		try {
			// 分析表达式
			List<Token> tokens = analyzer.analyze(expression);
			for (Token token : tokens) {
				if (Token.TokenType.VARIABLE != token.getTokenType()) {
					continue;
				}
				String VariableName = token.getVariable().getVariableName();
				variables.add(VariableName);
			}
			// 转化RPN，并验证
			tokens = compiler.compile(tokens);
			// 返回预编译表达式
			return new PreparedFuncExp(expression, tokens, VariableContainer.getVariableMap());
		} catch (Exception e) {
			log.error(e.getMessage());
		}

		return null;
	}

	public int invoke(Param in) {
		for (String name : variables) {
			// 获取变量的值
			String value = getVariableValue(in, SluiceCache.getVariable(name));
			try {
				// 设置变量及变量值,参与运算
				if (preparedFuncExp != null) {
					if (log.isDebugEnabled()) {
						log.debug(String.format("[Variable]name=%s, value=%s", name, value));
					}
					preparedFuncExp.setArgument(name, value);
				}
			} catch (Exception e) {
				log.error(e.getMessage());
			}
		}
		// 执行表达式，如果检正通过返回errcode,不通过则返回0.(因组件主要用于拦截数据，表达式中定义的是符合拦截规则的数据，不符合规则自然就不需要拦截处理)
		boolean result = false;
		if (preparedFuncExp != null) {
			result = (Boolean) preparedFuncExp.execute();
		}

		return result ? errcode : 0;
	}

	/**
	 * 获取变量值
	 * 
	 * @param in
	 * @param variable
	 * @return
	 */
	private String getVariableValue(Param in, String[] variable) {
		if (variable == null) {
			return null;
		}

		String value = null;
		String type = variable[0];
		String temp = variable[1];
		if ("CONST".equals(type)) {
			// 设置常量
			value = temp;
		} else if ("HEAD".equals(type)) {
			// 设置请求头数据
			String[] arr = StringUtils.split(temp, CharacterUtil.SEPARATOR);
			for (int i = 0; i < arr.length; i++) {
				value = in.getHead().getString("@" + arr[i]);
				if (StringUtils.isNotEmpty(value)) {
					break;
				}
			}
		} else if ("BODY".equals(type)) {
			// 设置请求内容
			String[] arr = StringUtils.split(temp, CharacterUtil.SEPARATOR);
			for (int i = 0; i < arr.length; i++) {
				value = in.getBody().getString("@" + arr[i]);
				if (StringUtils.isNotEmpty(value)) {
					break;
				}
			}
		}

		return value;
	}

}
