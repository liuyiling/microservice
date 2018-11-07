package com.liuyiling.microservice.api.exception;

import com.liuyiling.microservice.core.common.ExceptionEnum;
import com.liuyiling.microservice.core.exception.BusinessException;
import com.liuyiling.microservice.core.exception.InvalidParamException;
import com.liuyiling.microservice.core.exception.InvalidRequestException;
import com.liuyiling.microservice.core.util.JsonUtil;
import com.liuyiling.microservice.core.util.ResultBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

/**
 * @author liuyiling
 * @date on 2018/10/26
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 统一拦截处理业务异常
     * 状态码：400
     */
    @ExceptionHandler(BusinessException.class)
    public ResultBean<String> validExceptionHandler(BusinessException e) {
        logger.warn("业务异常：{}", e.getMessage(), e);
        return new ResultBean<>(ExceptionEnum.BUSINESS_ERROR, e.getErrStr(), e.getMessage(), JsonUtil.bean2Json(e.getData()));
    }

    /**
     * 统一处理Spring解析Bean验证抛出的参数校验异常
     * 状态码：401
     */
    @ExceptionHandler({BindException.class, HttpMessageNotWritableException.class})
    public ResultBean<List<FieldError>> bindExceptionHandler(BindException e) {
        logger.warn("参数校验失败,{}", JsonUtil.bean2Json(e.getTarget()));
        return new ResultBean<>(ExceptionEnum.ARGUMENTS_INVALID, null, e.getMessage(),
                e.getBindingResult().getFieldErrors());
    }

    /**
     * 参数校验失败，统一采用warn记录日志
     * 状态码：401
     */
    @ExceptionHandler({InvalidParamException.class})
    public ResultBean<List<FieldError>> bindExceptionHandler(InvalidParamException e) {
        logger.warn("参数校验失败,{}", e.getErrStr());
        return new ResultBean<>(ExceptionEnum.ARGUMENTS_INVALID, e.getErrStr(), e.getMessage(),
                null);
    }


    /**
     * 默认异常处理方法
     * 状态码：500
     */
    @ExceptionHandler({Exception.class, InvalidRequestException.class})
    @ResponseStatus
    public ResultBean<String> runtimeExceptionHandler(Exception e) {
        logger.error("运行时异常：【{}】", e.getMessage(), e);
        ResultBean<String> result = new ResultBean<String>();
        result.setCode(ExceptionEnum.SERVER_ERROR.getCode());
        result.setMessage(e.getMessage() + "-- traceid:" + MDC.get("traceId"));
        return new ResultBean<>(ExceptionEnum.SERVER_ERROR, null, e.getMessage(),
                null);
    }
}
