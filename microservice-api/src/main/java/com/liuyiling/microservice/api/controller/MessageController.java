package com.liuyiling.microservice.api.controller;

import com.liuyiling.microservice.api.model.Member;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;

/**
 * @author liuyiling
 * @date on 2018/10/25
 */
@Controller
public class MessageController {

    @GetMapping(value = "show")
    public String show(Model model) {
        model.addAttribute("url", "www.baidu.com");
        return "message/message_show";
    }

    @GetMapping(value = "add")
    @ResponseBody
    public Member add(@Valid Member member, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new Member();
        }else {
            return member;
        }
    }
}
