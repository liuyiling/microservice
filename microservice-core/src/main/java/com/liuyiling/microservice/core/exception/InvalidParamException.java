package com.liuyiling.microservice.core.exception;

import lombok.Data;

/**
 * 请求异常，包含的可能
 */
@Data
public class InvalidParamException extends Exception {
    private String errStr;

    public InvalidParamException(String errStr, String errMsg) {
        super(errMsg);
        this.errStr = errStr;
    }
}
