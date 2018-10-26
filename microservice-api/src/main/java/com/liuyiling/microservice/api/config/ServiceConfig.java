package com.liuyiling.microservice.api.config;

import com.liuyiling.microservice.api.service.MessageService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author liuyiling
 * @date on 2018/10/25
 */
@Configuration
public class ServiceConfig {

    @Bean
    public MessageService getMessageService(){
        return new MessageService();
    }
}
