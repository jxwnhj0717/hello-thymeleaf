package com.starter.thymeleaf.res;

import com.alibaba.excel.EasyExcel;
import com.google.common.primitives.Ints;
import com.starter.thymeleaf.fenbao.FenBaoTable;
import com.starter.thymeleaf.fenbao.LevelFragmemt;
import com.starter.thymeleaf.fenbao.PathCount;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Controller
public class ResourceController {

    @Autowired
    private ResourceRepository resourceRepository;

    @GetMapping("nav")
    public String nav(HttpServletRequest request) {
        return "nav";
    }

    @GetMapping("res")
    public String res(HttpServletRequest request) {
        return "res";
    }

    @GetMapping("resJson")
    @ResponseBody
    public ResourcePage resJson(@RequestParam(required = false) String uid,
                                @RequestParam(required = false) Integer offset,
                                @RequestParam(required = false) Integer limit) {
        List<Resource> resources = resourceRepository.getResources(uid, null, null);
        List<Resource> subResources = null;
        if(!resources.isEmpty()) {
            int fromIndex = offset == null ? 0 : Math.min(offset, resources.size() - 1);
            if(limit == null) limit = 1000;
            int toIndex = Math.min(fromIndex + limit, resources.size());
            subResources = resources.subList(fromIndex, toIndex);
        } else {
            subResources = resources;
        }
        ResourcePage page = new ResourcePage(resources.size(), subResources);
        return page;
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
