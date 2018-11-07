package com.liuyiling.microservice.core.config;

import com.alibaba.druid.support.http.WebStatFilter;

import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;

/**
 * @author liuyiling
 * @date on 2018/11/7
 */
@WebFilter(
        urlPatterns = "/demo1/*",
        initParams = {
                @WebInitParam(name = "exclusions", value = "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*")
        }
)
public class DruidStatFilter extends WebStatFilter {
}
