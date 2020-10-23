package esun.wharf.aspect;

import esun.wharf.exception.CustomHttpException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author test
 */
@Aspect
@Component
public class SystemAspect {
	//日志声明
	private static Logger logger= LoggerFactory.getLogger(SystemAspect.class);

	@Around("execution(* esun.wharf.controller.*.*(..))")
	public Object TokenCheck(ProceedingJoinPoint proceedingJoinPoint) throws Throwable{
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

		MethodSignature signature=(MethodSignature)proceedingJoinPoint.getSignature();
//        MethodSignature signature=(MethodSignature)proceedingJoinPoint.getSignature();
		Method method=signature.getMethod();
//        检测是否有name参数

		//获取函数参数和值
		Map<String,Object> parameterMap=new HashMap<>();
		Object[] args=proceedingJoinPoint.getArgs();
		String[] paraNames = signature.getParameterNames();
		for (int i = 0; i <paraNames.length ; i++) {
			parameterMap.put(paraNames[i],args[i]);
			logger.info(method.getName()+":"+paraNames[i]+":"+args[i].toString());
		}

		Optional tokenCheck=Optional.ofNullable(request.getHeader("Token-Checked"));
		if(!"true".equals(tokenCheck.orElse(""))){
			String message="请求错误错误,未经过token校验";
			logger.error(message);
			throw new CustomHttpException(message, HttpStatus.UNAUTHORIZED.value());
		}
		Object proceed = proceedingJoinPoint.proceed();
		return proceed;
	}
}
