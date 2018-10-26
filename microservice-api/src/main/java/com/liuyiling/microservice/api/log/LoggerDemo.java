package com.liuyiling.microservice.api.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author liuyiling
 * @date on 2018/10/26
 */
public class LoggerDemo {

    private static Logger logger = LoggerFactory.getLogger(LoggerDemo.class);

    /**
     * LoggerFactory.getLogger(LoggerDemo.class)会获取到name=com.liuyiling.microservice.api.log.LoggerDemo这个Logger
     * 该Logger向name=com.liuyiling.microservice.api.log传递日志
     * name=com.liuyiling.microservice.api.log向name=root传递日志
     *
     * @param args
     */
    public static void main(String[] args) {
        logger.debug("debug");
        logger.warn("warn");
        logger.info("info");
        logger.error("error");
    }
}
