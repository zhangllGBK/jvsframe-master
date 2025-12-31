package com.jarveis.frame.jdbc;

import com.jarveis.frame.jdbc.handler.MapListHandler;
import com.jarveis.frame.jdbc.handler.ValueHandler;
import com.jarveis.frame.util.JsonUtil;

import java.util.List;

public class DamengTest {

    public static void main(String[] args) throws Exception {
        JdbcParser parser = new JdbcParser();
        parser.parse();

        String sql = "select 1+1 from dual";
        Object result = JdbcUtil.query(sql, new ValueHandler());
        System.out.println("result = " + result);

        sql = "select * from c_ad_info";
        List list = (List) JdbcUtil.query(sql, new MapListHandler());
        for (Object obj : list) {
            System.out.println(JsonUtil.toJson(obj));
        }
    }
}
