package com.example.exception;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

import static org.springframework.core.annotation.AnnotatedElementUtils.findMergedAnnotation;

/**
 * 基础异常处理器
 * @author xiaoliebin
 */
@RestControllerAdvice
public class BaseExceptionHandler extends ResponseEntityExceptionHandler {

    private MessageSource messageSource;

    @Autowired
    public BaseExceptionHandler(MessageSource messageSource){
        this.messageSource=messageSource;
    }

    @ExceptionHandler
    public ResponseEntity<BaseExceptionDescription> handleBaseException(BaseException exception, HttpServletRequest request, HttpServletResponse response, Locale locale){
        HttpStatus responseStatus = resolveAnnotatedResponseStatus(exception);
        BaseExceptionDescription body = new BaseExceptionDescription(
                exception.getMessage(),
                messageSource.getMessage(exception.getMessage(), exception.getArgs(), locale),
                responseStatus.value(),
                request.getServletPath());
        return new ResponseEntity<>(body, responseStatus);
    }
    private HttpStatus resolveAnnotatedResponseStatus(Exception exception) {
        ResponseStatus annotation = findMergedAnnotation(exception.getClass(), ResponseStatus.class);
        if (annotation != null) {
            return annotation.value();
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
