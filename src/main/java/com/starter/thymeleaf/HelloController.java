package com.starter.thymeleaf;

import com.alibaba.excel.EasyExcel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;

@Controller
public class HelloController {

    @Autowired
    private ResourceRepository resourceRepository;

    @GetMapping("nav")
    public String nav(HttpServletRequest request) {
        return "nav";
    }

    @GetMapping("fenbao")
    public String fenbao(HttpServletRequest request) {
        return "fenbao";
    }

    @GetMapping("res")
    public String res(HttpServletRequest request, @RequestParam(required = false) String uid) {
        List<Resource> resources = resourceRepository.getResources(uid);
        request.setAttribute("uid", uid);
        request.getSession().setAttribute("resources", resources);
        return "res";
    }

    @GetMapping("resDownload")
    public void resDownload(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/x-excel");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=res.xlsx");

        List<Resource> resources = (List<Resource>) request.getSession().getAttribute("resources");

        File file = File.createTempFile("res", "xlsx");
        EasyExcel.write(file, Resource.class).sheet("data").doWrite(resources);

        try(BufferedInputStream in = new BufferedInputStream(new FileInputStream(file))) {
            BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
            StreamUtils.copy(in, out);
        }


    }


}
