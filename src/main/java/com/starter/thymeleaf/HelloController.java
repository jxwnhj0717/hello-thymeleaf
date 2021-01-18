package com.starter.thymeleaf;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class HelloController {

    @GetMapping("hello")
    public String hello(HttpServletRequest request) {
        request.setAttribute("name", "huangjin");
        return "hello";
    }

}
