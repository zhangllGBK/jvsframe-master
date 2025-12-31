package com.jarveis.frame.jdbc.ant;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 可持久化类注释
 * 
 * @author liuguojun
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {

	String name() default ""; // 标识表的名称
}
