package com.example.annotation;

import java.lang.annotation.*;

/**
 * 路由权限校验
 * @author John.xiao
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Router {
    String name();
}
