package com.liuyiling.microservice.core.distributedlock;

/**
 * @author liuyiling
 * @date on 2018/11/7
 */
public enum LockType {
    /** 可重入锁*/
    REENTRANT_LOCK,

    /** 公平锁*/
    FAIR_LOCK,

    /** 读锁*/
    READ_LOCK,

    /** 写锁*/
    WRITE_LOCK;
}
