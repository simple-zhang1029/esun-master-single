package esun.core.aspect;

import esun.core.annotation.LoginRequire;
import esun.core.annotation.Router;
import esun.core.constant.Message;
import esun.core.exception.CustomHttpException;
import esun.core.service.TokenService;
import esun.core.utils.MessageUtil;
import esun.core.utils.ResultUtil;
import esun.core.utils.RouterUtil;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 系统切面类
 */
@Aspect
@Component
public class SystemAspect {
    //日志声明
    private static Logger logger= LoggerFactory.getLogger(SystemAspect.class);

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    TokenService tokenService;

    /**
     * token校验切面
     * @return
     */
    @Around("execution(* esun.core.controller.*.*(..))")
    public Object TokenCheckAop(ProceedingJoinPoint proceedingJoinPoint) throws Throwable{
        MethodSignature signature=(MethodSignature)proceedingJoinPoint.getSignature();
        Method method=signature.getMethod();
        //获取函数参数和值
        Map<String,Object> parameterMap=new HashMap<>();
        Object[] args=proceedingJoinPoint.getArgs();
        String[] paraNames = signature.getParameterNames();
        for (int i = 0; i <paraNames.length ; i++) {
            parameterMap.put(paraNames[i],args[i]);
        }
        //检测是否有name参数
//        String name=parameterMap.get("name").toString();
//        if (StringUtils.isBlank(name)){
//            name="system";
//        }
        // 修改日志
//        MDC.put("user",name);
        //检查是否有LoginRequire标签
        LoginRequire loginAnnotation=method.getAnnotation(LoginRequire.class);
        if(loginAnnotation == null){
            //执行被请求函数
            Object proceed = proceedingJoinPoint.proceed();
            return proceed;
        }
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        //获取用户名
        String name=request.getHeader("name");
        if(StringUtils.isBlank(name)){
            name = request.getParameter("name");
        }
        //获取token
        String token=request.getHeader("token");
        if(StringUtils.isBlank(token)){
            token = request.getParameter("token");
        }
        //监测登入,登入列表保存在redis的loginSet中
//        if(!redisTemplate.opsForSet().isMember("loginSet", name)){
//            String message=MessageUtil.getMessage(Message.USER_NOT_LOGIN.getCode());
//            logger.info(message);
//            throw new CustomHttpException(message, HttpStatus.UNAUTHORIZED.value());
//        }

        //校验请求路由
        Router router=method.getAnnotation(Router.class);
        if (router!=null){
            if (!RouterUtil.verifyRouter(name,router.name())){
                String message=MessageUtil.getMessage(Message.ROUTER_CHECK_ERROR.getCode());
                logger.error(message);
                throw new CustomHttpException(message, HttpStatus.UNAUTHORIZED.value());
            }
            String message=MessageUtil.getMessage(Message.ROUTER_CHECK_SUCCESS.getCode());
            logger.info(message);
        }
        //校验是否存在token参数
        if(StringUtils.isBlank(token)){
            String message=MessageUtil.getMessage(Message.TOKEN_IS_NULL.getCode());
            logger.error(message);
            throw new CustomHttpException(message, HttpStatus.UNAUTHORIZED.value());
        }
        //使用tokenUtil进行校验
        ResultUtil result=tokenService.checkToken(token);
        if (HttpStatus.OK.value() != (int)result.get("code")){
            String message=MessageUtil.getMessage(Message.TOKEN_CHECK_ERROR.getCode());
            logger.error(message);
            throw new CustomHttpException(message, HttpStatus.UNAUTHORIZED.value());
        }
        //校验成功
        String message=MessageUtil.getMessage(Message.TOKEN_CHECK_SUCCESS.getCode());
        logger.error(message);

        //执行被请求函数
        Object proceed = proceedingJoinPoint.proceed();
        return proceed;
    }
}
