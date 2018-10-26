package com.liuyiling.microservice.api.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static java.lang.System.out;

/**
 * 业务层拦截器,最常使用-环绕通知
 * @author liuyiling
 * @date on 2018/10/26
 */
@Aspect
@Component
public class ServiceAdapter {

    @Around("execution(* com.liuyiling.microservice.*.*(..))")
    public Object arroundInvoke(ProceedingJoinPoint point) throws Throwable {
        out.println("Service-Before" + Arrays.toString(point.getArgs()));
        Object obj = point.proceed(point.getArgs());
        return obj;
    }
}
