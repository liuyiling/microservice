package com.liuyiling.microservice.core.exception;


import lombok.Data;

/**
 * 业务异常,对应BUSINESS_ERROR(400)
 */
@Data
public class BusinessException extends RuntimeException {

    /**
     * 业务异常代码
     */
    private String errStr;

    /**
     * 异常数据
     */
    private Object data;
}