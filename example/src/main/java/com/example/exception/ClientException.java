package com.example.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 客户端异常
 * @author xiaoliebin
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ClientException extends BaseException {

    public ClientException() {
        super();
    }

    public ClientException(String message) {
        super(message);
    }

    public ClientException(String message, Object[] args) {
        super(message, args);
    }

}
