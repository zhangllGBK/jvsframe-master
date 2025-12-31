package com.jarveis.frame.jdbc.ant;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 分表策略
 *
 * @author liuguojun
 * @since 2024-08-06
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TableShardStrategy {

	String column(); // 分表字段

	String algorithm(); // 算法实现类
}
