package com.liuyiling.microservice.core.distributedlock;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 自定义的分布式锁注释
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DistributedLock {

    /**
     * 锁的资源，key
     **/
    @AliasFor("lockName")
    String lockName() default "'default'";

    /**
     * 锁类型
     */
    LockType lockType() default LockType.REENTRANT_LOCK;

    /**
     * 获取锁等待时间，默认3秒
     */
    long waitTime() default 1000L;

    /**
     * 锁自动释放时间，默认30秒
     */
    long leaseTime() default 30000L;

    /**
     * 时间单位（获取锁等待时间和持锁时间都用此单位）
     */
    TimeUnit unit() default TimeUnit.MILLISECONDS;
}