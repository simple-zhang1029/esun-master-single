package esun.dbhelper.exception;


import esun.dbhelper.utils.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

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

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResultUtil handlerNoFoundException(Exception e) {
        logger.error(e.getMessage(), e);
        return ResultUtil.error(404, "路径不存在，请检查路径是否正确");
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResultUtil handleDuplicateKeyException(DuplicateKeyException e){
        logger.error(e.getMessage(), e);
        return ResultUtil.error("数据库中已存在该记录");
    }

    @ExceptionHandler(Exception.class)
    public ResultUtil handleException(Exception e){
        logger.error(e.getMessage(), e);
        return ResultUtil.error();
    }
}
