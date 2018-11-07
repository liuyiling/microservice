package com.liuyiling.microservice.core.readlock;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.redisson.RedissonRedLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

import static java.lang.System.out;

@Aspect
@Component
public class RedisRedLockAspect {

    public static Logger logger = LoggerFactory.getLogger(RedisRedLockAspect.class);

    @Autowired
    private RedissonClient client;

    /**
     * 切入点为带有redisRedLock的注释
     *
     * @param redisRedLock
     */
    @Pointcut("@annotation(redisRedLock)")
    public void cut(RedisRedLock redisRedLock) {
    }

    @Around("cut(redisRedLock)")
    public Object Redlock(ProceedingJoinPoint pjp, RedisRedLock redisRedLock) {
        Object result = null;
        String prefix = pjp.getSignature().toShortString();
        RLock lock1 = client.getLock(prefix.concat("{lock1}"));

        RedissonRedLock lock = new RedissonRedLock(lock1);

        try {
            out.println("Test. " + Thread.currentThread().getId() + " trying get lock.");
            boolean res = lock.tryLock(redisRedLock.waitTime(), redisRedLock.leaseTime(), TimeUnit.MILLISECONDS);
            out.println("Test. " + Thread.currentThread().getId() + " already get lock.");
            if (res) {
                //获得锁进行操作
                result = pjp.proceed();
            }
        } catch (Throwable e) {
            logger.warn(Thread.currentThread().getName() + " failed get lock");
        } finally {
            lock.unlock();
        }
        return result;
    }
}
