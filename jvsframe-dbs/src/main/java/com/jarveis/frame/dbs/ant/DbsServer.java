package com.jarveis.frame.dbs.ant;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 服务器应用注解
 *
 * @author liuguojun
 * @since 2022-06-29
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DbsServer {

    public int httpPort() default 8080;

    public String contextPath() default "/";

    public String staticPath() default "static";

    public int acceptors() default -1;

    public int selectors() default -1;

    public int poolSize() default 200;
}
