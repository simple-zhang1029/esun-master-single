package esun.dbhelper.aspect;

import esun.dbhelper.dataSources.DataSource;
import esun.dbhelper.dataSources.DataSourceType;
import esun.dbhelper.service.CommonService;
import esun.dbhelper.utils.DataSourceUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
public class ControllerAspect {

    @Autowired
    private CommonService commonService;
    /**
     * Controller切面类，用于拦截和校验对接口的请求
     * @param proceedingJoinPoint
     * @return
     * @throws Throwable
     */
    @Around("execution(* esun.dbhelper.controller.*.*(..))")
    public Object ControllerAop(ProceedingJoinPoint proceedingJoinPoint) throws Throwable{
        String dataSource;
        String dataSourceType;
        MethodSignature signature=(MethodSignature) proceedingJoinPoint.getSignature();
        Method method=signature.getMethod();
        DataSource dataSourceAnnotation = method.getAnnotation(DataSource.class);
        DataSourceType dataSourceTypeAnnotation = method.getAnnotation(DataSourceType.class);
        Map<String,Object> parameterMap=new HashMap<>();
        Object[] args=proceedingJoinPoint.getArgs();
        String[] paraNames = signature.getParameterNames();
        for (int i = 0; i <paraNames.length ; i++) {
            parameterMap.put(paraNames[i],args[i]);
        }
        if (dataSourceAnnotation!=null){
            dataSource=dataSourceAnnotation.name();
        }
        else {
            dataSource= commonService.dataSource(parameterMap.get("product").toString());
        }
        if (dataSourceTypeAnnotation!=null){
            dataSourceType=dataSourceTypeAnnotation.name();
        }
        else {
            dataSourceType="slave";
        }
        DataSourceUtil.setDatasource(dataSource,dataSourceType);
        DataSourceUtil.checkIndex(parameterMap.get("sql").toString(),dataSource);
        Object result = proceedingJoinPoint.proceed();
        return result;

    }

}

