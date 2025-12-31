package com.jarveis.frame.jdbc;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.jarveis.frame.jdbc.handler.ArrayHandler;
import com.jarveis.frame.jdbc.handler.ArrayListHandler;
import com.jarveis.frame.jdbc.handler.ValueHandler;
import com.jarveis.frame.jdbc.handler.ValueListHandler;

/**
 * @desc Jdbc单元测试
 * @author liuguojun
 * @create 2018-04-10
 */
public class JdbcTest {

	//@Before
	public void before() throws Exception {
		JdbcParser parser = new JdbcParser();
		parser.parse();
	}

	//@Test
	public void testQuery() throws Exception {
		String sql = "select 1+1 from dual";
		Object result = JdbcUtil.query(sql, new ValueHandler());
		System.out.println("value=" + result);
		if (result instanceof Long) {
			Assert.assertEquals(2, ((Long) result).intValue());
		} else {
			Assert.assertEquals(2, ((BigDecimal) result).intValue());
		}
	}

	//@Test
	public void testValueHandler() throws Exception {
		String sql = "select CREATE_DATE from C_SMS";
		Date result = (Date) JdbcUtil.query(sql, new ValueHandler());
		System.out.println("value=" + result);

		Assert.assertEquals(2, 2);
	}

	//@Test
	public void testValueListHandler() throws Exception {
		String sql = "select CREATE_DATE from C_SMS";
		List result = (List) JdbcUtil.query(sql, new ValueListHandler());
		for (Object obj : result) {
			System.out.println("value=" + obj);
		}

		Assert.assertEquals(2, 2);
	}

	//@Test
	public void testArrayHandler() throws Exception {
		String sql = "select * from C_SMS";
		Object[] result = (Object[]) JdbcUtil.query(sql, new ArrayHandler());
		for (Object obj : result) {
			System.out.println("value=" + obj);
		}

		Assert.assertTrue("有"+result.length+"字段", result.length > 0);
	}

	//@Test
	public void testArrayListHandler() throws Exception {
		String sql = "select * from C_SMS";
		List result = (List) JdbcUtil.query(sql, new ArrayListHandler());
		for (Object obj : result) {
			System.out.println("value[5]=" + ((Object[]) obj)[5]);
		}

		Assert.assertTrue("有"+result.size()+"条数据", result.size() > 0);
	}

}
