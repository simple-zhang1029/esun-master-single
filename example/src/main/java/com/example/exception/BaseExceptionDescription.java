package com.example.exception;

import java.util.Date;


/**
 * 基础异常类描描述属性
 * @author xiaoliebin
 */
public class BaseExceptionDescription {

    private String error;
    private String message;
    private Integer status;
    private String path;
    private Date timestamp;

    BaseExceptionDescription(String error, String message, Integer status, String path) {
        this.error = error;
        this.message = message;
        this.status = status;
        this.path = path;
        this.timestamp = new Date();
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

}
