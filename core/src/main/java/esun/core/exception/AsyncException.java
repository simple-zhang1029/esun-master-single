package esun.core.exception;


/**
 * 异步异常类
 * @author xiaoliebin
 */
public class AsyncException extends BaseException {
    private int code;
    private String msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
