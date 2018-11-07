package com.liuyiling.microservice.core.readlock;

import java.lang.annotation.*;

/**
 * 自定义的分布式锁注释
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedisRedLock {


    /**
     * 轮询锁的时间超时时常, 单位: ms
     */
    int waitTime() default 2000;

    /**
     * redis-key失效时常, 单位: ms
     */
    int leaseTime() default 3000;
}