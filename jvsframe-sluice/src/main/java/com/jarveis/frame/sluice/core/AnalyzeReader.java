package com.jarveis.frame.sluice.core;

import java.io.IOException;
import java.io.StringReader;
import java.nio.CharBuffer;

import com.jarveis.frame.sluice.core.datameta.Element;
import com.jarveis.frame.sluice.core.reader.ElementReader;
import com.jarveis.frame.sluice.core.reader.ElementReaderFactory;

/**
 * 表达式读取器
 *
 * @author liuguojun
 * @since  2018-07-25
 */
public class AnalyzeReader extends StringReader {

	private static final String IGNORE_CHAR = " \r\n\t";// 词元间的忽略字符

	private int currentIndex = 0;// 当前索引
	private int markIndex = 0;// 被标记后索引
	private boolean prefixBlank = false;// 与上一个读到的Element之间是否有空格

	public AnalyzeReader(String s) {
		super(s);
	}

	/**
	 * 取得当前位置
	 * 
	 * @return
	 */
	public int getCruuentIndex() {
		return currentIndex;
	}

	/**
	 * Element之前是否有空格
	 * 
	 * @return
	 */
	public boolean isPrefixBlank() {
		return prefixBlank;
	}

	public void setPrefixBlank(boolean prefixBlank) {
		this.prefixBlank = prefixBlank;
	}

	@Override
	public int read() throws IOException {
		int c = super.read();
		if (c != -1) {
			currentIndex++;
			markIndex++;
		}
		return c;
	}

	@Override
	public int read(char[] cbuf) throws IOException {
		int c = super.read(cbuf);
		if (c > 0) {
			currentIndex += c;
			markIndex += c;
		}
		return c;
	}

	@Override
	public int read(CharBuffer target) throws IOException {
		int c = super.read(target);
		if (c > 0) {
			currentIndex += c;
			markIndex += c;
		}
		return c;
	}

	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		int c = super.read(cbuf, off, len);
		if (c > 0) {
			currentIndex += c;
			markIndex += c;
		}
		return c;
	}

	@Override
	public void reset() throws IOException {
		super.reset();
		currentIndex = currentIndex - markIndex;
	}

	@Override
	public void mark(int readAheadLimit) throws IOException {
		super.mark(readAheadLimit);
		markIndex = 0;
	}

	/**
	 * 读取一个词元素
	 * 
	 * @return Element
	 * @throws Exception
	 */
	public Element readToken() throws Exception {
		prefixBlank = false;
		while (true) {
			// 去除空格
			mark(0);// 标记
			int b = read();
			if (b == -1) {
				return null;
			}
			char c = (char) b;
			if (IGNORE_CHAR.indexOf(c) >= 0) {// 去除开始的空格
				prefixBlank = true;
				continue;
			}
			reset();// 重置

			// 构造一个词元读取器
			ElementReader er = ElementReaderFactory.createElementReader(this);

			return er.read(this);
		}
	}

}
