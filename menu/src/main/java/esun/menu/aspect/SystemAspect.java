package esun.menu.aspect;

import esun.menu.exception.CustomHttpException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * @author test
 */
@Aspect
@Component
public class SystemAspect {
	//日志声明
	private static Logger logger= LoggerFactory.getLogger(SystemAspect.class);

	@Around("execution(* esun.menu.controller.*.*(..))")
	public Object TokenCheck(ProceedingJoinPoint proceedingJoinPoint) throws Throwable{
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
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
