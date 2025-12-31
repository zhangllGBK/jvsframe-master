package com.jarveis.frame.jdbc.ant;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 字段注释
 * 
 * @author liuguojun
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {

	String name() default ""; // 标识字段名称

	boolean primaryKey() default false; // 是否是主键

}
