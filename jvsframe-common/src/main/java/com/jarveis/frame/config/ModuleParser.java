package com.jarveis.frame.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 解析器类的注解
 *
 * @author liuguojun
 * @since 2022-06-29
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ModuleParser {

    /**
     * 配置文件
     *
     * @return
     */
    public String file() default "";

    /**
     * 动态刷新后,是否重新加载
     *
     * @return
     */
    public boolean refreshLoad() default false;
}
