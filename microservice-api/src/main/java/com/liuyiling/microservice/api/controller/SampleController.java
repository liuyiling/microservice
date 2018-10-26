package com.liuyiling.microservice.api.controller;

import com.liuyiling.microservice.api.service.MessageService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author liuyiling
 * @date on 2018/10/24
 */
@RestController
public class SampleController extends AbstractBaseController{

    //如果有两个未命名的实体对象，必须写name属性
    @Resource
    private MessageService messageService;

    @GetMapping(value = "/echo/{message}")
    public String home(@PathVariable("message") String message) {
        return super.getMessage("welcome.msg", message);
    }

    @GetMapping(value = "/object")
    public String object(HttpServletRequest request, HttpServletResponse response) {
        return "www:" + request.getRemoteUser();
    }

}
