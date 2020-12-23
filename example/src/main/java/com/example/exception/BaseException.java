package com.example.exception;


/**
 * 基础异常类
 * @author xiaoliebin
 */
public class BaseException extends Exception {
    private Object[] args;

    public BaseException(){
        super();
    }

    public  BaseException(String msg){
        super(msg);
    }

    public BaseException(String msg, Object[] args){
        super(msg);
        this.args = args;
    }


    public BaseException(String msg,Throwable e){
        super(msg,e);
    }

    public BaseException(String msg, Object[] args, Throwable e){
        super(msg,e);
        this.args=args;
    }

    public Object[] getArgs(){
        return args;
    }


}
