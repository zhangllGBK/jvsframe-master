package com.jarveis.frame.sluice.core.datameta;

/**
 * 表达式分析诩元素
 *
 * @author liuguojun
 * @since  2018-07-25
 */
public class Element {

	public enum ElementType {
		// 字符窜
		STRING,
		// 变量
		VARIABLE,
		// 函数
		FUNCTION,
		// 分隔符
		SPLITOR
	}

	private String text;
	private ElementType type;
	private int index;

	public Element(String text, int index, ElementType type) {
		this.text = text;
		this.index = index;
		this.type = type;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public ElementType getType() {
		return type;
	}

	public void setType(ElementType type) {
		this.type = type;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
}
