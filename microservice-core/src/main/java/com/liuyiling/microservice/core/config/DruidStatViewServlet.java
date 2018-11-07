package com.liuyiling.microservice.core.config;

import com.alibaba.druid.support.http.StatViewFilter;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;

/**
 * @author liuyiling
 * @date on 2018/11/7
 */
@WebServlet(
        urlPatterns = {"/druid/*"},
        initParams = {
                @WebInitParam(name = "loginUsername", value = "admin"),
                @WebInitParam(name = "loginPassword", value = "test"),
                @WebInitParam(name = "resetEnable", value = "false")
//      @WebInitParam(name = "allow", value = "127.0.0.1")
        }
)
public class DruidStatViewServlet extends StatViewFilter {
}
