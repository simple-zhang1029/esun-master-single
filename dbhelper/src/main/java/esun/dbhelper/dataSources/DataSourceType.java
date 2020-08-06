package esun.dbhelper.dataSources;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataSourceType {
    String name() default "slave";

}
