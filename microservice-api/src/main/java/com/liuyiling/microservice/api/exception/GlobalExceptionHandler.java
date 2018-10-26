package com.liuyiling.microservice.api.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * @author liuyiling
 * @date on 2018/10/26
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    public static final String DEFAULT_ERROR_VIEW = "error";

    @ExceptionHandler(value = Exception.class)
    public Object defaultExceptionHandler(HttpServletRequest request, Exception e) {
        return new Object();
    }
}
