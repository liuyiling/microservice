package com.liuyiling.microservice.core.util;

import com.liuyiling.microservice.core.common.ExceptionEnum;

import java.io.Serializable;


public class ResultBean<T> implements Serializable {

    /**
     * 整体异常
     */
    private int code = ExceptionEnum.SUCCESS.getCode();

    /**
     * 细分异常信息
     */
    private String errStr;

    /**
     * 消息内容
     */
    private String message = "success";

    /**
     * 数据内容
     */
    private T data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getErrStr() {
        return errStr;
    }

    public void setErrStr(String errStr) {

        this.errStr = errStr;
    }

    public ResultBean(ExceptionEnum exceptionEnum, String errStr, String message, T data) {

        this.code = exceptionEnum.getCode();
        this.errStr = errStr;
        this.message = message;
        this.data = data;
    }


    public ResultBean() {
    }

    public ResultBean(T data) {
        super();

        this.data = data;
    }

    public ResultBean(Throwable e) {
        this.code = ExceptionEnum.SERVER_ERROR.getCode();

        this.message = e.getMessage();
    }
}
