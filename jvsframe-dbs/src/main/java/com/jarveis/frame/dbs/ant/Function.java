package com.jarveis.frame.dbs.ant;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 功能类注释
 * 
 * @author liuguojun
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Function {

	/**
	 * 功能编号
	 *
	 * @return
	 */
	public String code() default "";

	/**
	 * 函数的作用域
	 *
	 * @return
	 */
	public Scope scope() default Scope.PUBLIC;

	/**
	 * 事务支持
	 *
	 * @return
	 */
	public boolean transaction() default false;
	
}
