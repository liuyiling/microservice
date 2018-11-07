package com.liuyiling.microservice.core.util;

import org.springframework.beans.factory.annotation.Value;

/**
 * @author liuyiling
 * @date on 2018/11/7
 */
public class KeyUtil {

    @Value("${spring.application.name}")
    private static String applicationName;

    public static String getUserKey(String userId) {
        return applicationName + "_u" + userId;
    }

}
