package com.liuyiling.microservice.core.distributedlock;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.RedissonRedLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import static java.lang.System.out;

@Aspect
@Component
public class DistributedLockAspect {

    public static Logger logger = LoggerFactory.getLogger(DistributedLockAspect.class);

    @Autowired
    private RedissonClient redissonClient;

    private ExpressionParser parser = new SpelExpressionParser();

    private LocalVariableTableParameterNameDiscoverer discoverer = new LocalVariableTableParameterNameDiscoverer();

    /**
     * 切入点为带有redisRedLock的注释
     */
    @Pointcut("@annotation(com.liuyiling.microservice.core.distributedlock.DistributedLock)")
    public void pointCut() {
    }

    @Around("pointCut()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);
        String key = method.getName() + distributedLock.lockName();

        RLock lock = getLock(key, distributedLock);
        if (!lock.tryLock(distributedLock.waitTime(), distributedLock.leaseTime(), distributedLock.unit())) {
            out.println("get lock failed [{}]" + Thread.currentThread().getId());
            return null;
        }

        //得到锁,执行方法，释放锁
        out.println("get lock success [{}]" + Thread.currentThread().getId());
        try {
            return pjp.proceed();
        } catch (Exception e) {
            logger.error("execute locked method occured an exception", e);
        } finally {
            lock.unlock();
            out.println("release lock [{}]" + Thread.currentThread().getId());
        }
        return null;
    }

    private RLock getLock(String key, DistributedLock distributedLock) {
        switch (distributedLock.lockType()) {
            case REENTRANT_LOCK:
                return redissonClient.getLock(key);

            case FAIR_LOCK:
                return redissonClient.getFairLock(key);

            case READ_LOCK:
                return redissonClient.getReadWriteLock(key).readLock();

            case WRITE_LOCK:
                return redissonClient.getReadWriteLock(key).writeLock();

            default:
                throw new RuntimeException("do not support lock type:" + distributedLock.lockType().name());
        }
    }
}
