package com.liuyiling.microservice.core.exception;

import lombok.Data;

/**
 * 非法请求异常
 */
@Data
public class InvalidRequestException extends Exception {
    private String errStr;

    public InvalidRequestException(String errStr, String errMsg) {
        super(errMsg);
        this.errStr = errStr;
    }
}
