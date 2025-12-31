package com.jarveis.frame.dbs.ant;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 后置拦截注释
 * 
 * @author liuguojun
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface After {

	public String filters() default ""; // 功能编号

}
