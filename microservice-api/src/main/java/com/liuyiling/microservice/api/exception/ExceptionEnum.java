package com.liuyiling.microservice.api.exception;

/*
* 通用异常代码枚举
* success 正常
*
* */
public enum ExceptionEnum {

    SUCCESS(200),
    RESOURCE_NOT_FOUND(404),
    ARGUMENTS_INVALID(401),
    BUSINESS_ERROR(400),
    SERVER_ERROR(500);


    private ExceptionEnum(int code){
        this.code=code;
    }
    // 成员变量
    private int code;
    public   int getCode(){
        return  this.code;
    }
}
