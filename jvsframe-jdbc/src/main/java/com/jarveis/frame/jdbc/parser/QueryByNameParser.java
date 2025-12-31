package com.jarveis.frame.jdbc.parser;

import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Map;

import com.jarveis.frame.util.CharacterUtil;
import org.apache.commons.io.IOUtils;

/**
 * 对象的查询语句解析类
 * 
 * @author liuguojun
 */
public class QueryByNameParser extends AbstractParser {

	private static final String splitChar = " ,()";
	private Map map;

	public QueryByNameParser(Map map) {
		this.map = map;
	}

	public Object parse(Serializable s) throws Exception {
		ArrayList<Object> params = new ArrayList<Object>();

		InputStream is = IOUtils.toInputStream((String)s, CharacterUtil.UTF8);
		StringBuffer sql = new StringBuffer();
		StringBuffer name = new StringBuffer();
		boolean isName = false;
		int b;
		while ((b = is.read()) != -1) {
			char c = (char) b;
			if (c == ':') {
				isName = true;
			} else if (isName) {
				if (splitChar.indexOf(c) > -1) {
					if (name.length() == 0) {
						throw new Exception(":号后面不能跟随非法字符");
					}
					isName = false;
					params.add(map.get(name.toString()));
					sql.append("?").append(c);
					name = name.delete(0, name.length());
				} else {
					name.append(c);
				}
			} else {
				sql.append(c);
			}
		}
		
		if (isName) {
			if (name.length() == 0) {
				throw new Exception(":号后面不能跟随非法字符");
			}
			isName = false;
			params.add(map.get(name.toString()));
			sql.append("?");
			name = name.delete(0, name.length());
		}

		return new Object[] { sql.toString(), params.toArray() };
	}

}
