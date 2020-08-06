package esun.core.annotation;

import java.lang.annotation.*;

/**
 * 登入校验注解
 * @author xiaoliebin
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LoginRequire {
}
