package com.liuyiling.microservice.core.common;

/**
 * 通用异常代码枚举
 */
public enum ExceptionEnum {

    SUCCESS(200),
    BUSINESS_ERROR(400),
    ARGUMENTS_INVALID(401),
    SERVER_ERROR(500);

    ExceptionEnum(int code) {
        this.code = code;
    }

    private int code;

    public int getCode() {
        return this.code;
    }
}
