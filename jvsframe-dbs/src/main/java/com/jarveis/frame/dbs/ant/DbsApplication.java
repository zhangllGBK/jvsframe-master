package com.jarveis.frame.dbs.ant;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 应用程序注解
 *
 * @author liuguojun
 * @since 2022-06-29
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DbsApplication {

    public String scanPackage() default "";
}
