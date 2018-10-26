package com.liuyiling.microservice.api.controller;

import org.springframework.context.MessageSource;

import javax.annotation.Resource;
import java.util.Locale;

/**
 * @author liuyiling
 * @date on 2018/10/25
 */
public abstract class AbstractBaseController {
    @Resource
    private MessageSource messageSource;

    public String getMessage(String key, String... args) {
        return this.messageSource.getMessage(key, args, Locale.getDefault());
    }
}
