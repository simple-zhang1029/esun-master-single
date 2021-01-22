package com.example.annotation;

import java.lang.annotation.*;

/**
 * 登入校验注解
 * @author john.xiao
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LoginRequire {
}
