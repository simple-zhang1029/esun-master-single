package com.example.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

import java.lang.reflect.Method;

public class AsyncExceptionHandler implements AsyncUncaughtExceptionHandler {
    private  final  static Logger logger= LoggerFactory.getLogger(AsyncExceptionHandler.class);
    @Override
    public void handleUncaughtException(Throwable throwable, Method method, Object... objects) {
        logger.info("Async method has uncaught exception, params: " + objects);

        if(throwable instanceof  AsyncException){
            AsyncException asyncException=(AsyncException)throwable;
            logger.info("asyncException:" + asyncException.getMsg());
        }
        logger.error("Exception :", throwable);
    }
}
