package esun.dbhelper.dataSources;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 多数据源，切面处理类
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2017/9/16 22:20
 */
@Aspect
@Component
public class DataSourceAspect implements Ordered {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Pointcut("@annotation(esun.dbhelper.dataSources.DataSource)")
    public void dataSourcePointCut() {

    }

    @Around("dataSourcePointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();

        DataSource ds = method.getAnnotation(DataSource.class);
        if(ds == null){
            DynamicDataSource.setDataSource(DataSourceNames.POSTGRES_MASTER);
            logger.debug("set datasource is " + DataSourceNames.POSTGRES_MASTER);
        }else {
            DynamicDataSource.setDataSource(ds.name());
            logger.debug("set datasource is " + ds.name());
        }

        try {
            return point.proceed();
        } finally {
            DynamicDataSource.clearDataSource();
            logger.debug("clean datasource");
        }
    }

//    @Around("@annotation(esun.dbhelper.dataSources.DataSourceType)")
//    public Object aroundMethod(ProceedingJoinPoint proceedingJoinPoint)
//            throws Throwable
//    {
//        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
//        Method method = signature.getMethod();
//        DataSourceType dataSourceType = method.getAnnotation(DataSourceType.class);
//        DynamicDataSource.setDatasourceType(dataSourceType.name());
//        Object result = proceedingJoinPoint.proceed();
//        DynamicDataSource.clearDatasourceType();
//        return result;
//    }



    @Override
    public int getOrder() {
        return 1;
    }
}
