package com.starter.thymeleaf;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class HelloController {

    @GetMapping("nav")
    public String nav(HttpServletRequest request) {
        return "nav";
    }

    @GetMapping("fenbao")
    public String fenbao(HttpServletRequest request) {
        return "fenbao";
    }

    @GetMapping("res")
    public String res(HttpServletRequest request) {
        return "res";
    }

}
