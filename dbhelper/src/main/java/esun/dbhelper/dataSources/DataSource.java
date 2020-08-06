package esun.dbhelper.dataSources;

import java.lang.annotation.*;

/**
 * 数据源注解
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2017/9/16 22:20
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataSource {
    String name() default "";


}
