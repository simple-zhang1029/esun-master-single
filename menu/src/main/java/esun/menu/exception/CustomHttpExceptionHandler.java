package esun.menu.exception;



import esun.menu.utils.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/**
 * 自定义异常处理类
 * @author xiaoliebin
 */
@RestControllerAdvice
public class CustomHttpExceptionHandler {
    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 处理自定义异常
     */
    @CrossOrigin
    @ExceptionHandler(CustomHttpException.class)
    public ResultUtil handleCustomHttpException(CustomHttpException e){
        ResultUtil resultUtil = new ResultUtil();
        resultUtil.put("code", e.getCode());
        resultUtil.put("msg", e.getMessage());
        return resultUtil;
    }


}
