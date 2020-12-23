package com.example.annotation;

import java.lang.annotation.*;

/**
 * 路由权限校验
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Router {
    String name();
}
