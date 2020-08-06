package esun.core.exception;

/**
 * 自定义Http异常类
 * @author xiaoliebin
 */
public class CustomHttpException  extends RuntimeException{

    private String msg;

    private int code = 500;

    public CustomHttpException(String msg){
        super(msg);
        this.msg = msg;
    }

    public CustomHttpException(String msg, Throwable e){
        super(msg);
        this.msg = msg;
    }

    public CustomHttpException(String msg, int code){
        super(msg);
        this.msg = msg;
        this.code = code;
    }

    public CustomHttpException(String msg, int code, Throwable e){
        super(msg);
        this.msg = msg;
        this.code =code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
