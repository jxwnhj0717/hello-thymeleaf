package com.starter.thymeleaf;

import com.alibaba.excel.EasyExcel;
import com.google.common.primitives.Ints;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class ResourceController {

    @Autowired
    private ResourceRepository resourceRepository;

    @GetMapping("nav")
    public String nav(HttpServletRequest request) {
        return "nav";
    }

    @GetMapping("res")
    public String res(HttpServletRequest request, @RequestParam(required = false) String uid) {
        // List<Resource> resources = resourceRepository.getResources(uid);
        request.setAttribute("uid", uid);
        return "res";
    }

    @GetMapping("resJson")
    @ResponseBody
    public ResourcePage resJson(@RequestParam(required = false) String uid,
                                @RequestParam(required = false) Integer offset,
                                @RequestParam(required = false) Integer limit) {
        List<Resource> resources = resourceRepository.getResources(uid);
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


    @GetMapping("fenbao")
    public String fenbao(HttpServletRequest request,
                         @RequestParam(required = false) String uid,
                         @RequestParam(required = false) String lvls) {

        List<Resource> resources = resourceRepository.getResources(uid);

        // 按路径分类统计
        Map<Integer, Map<String, Integer>> fenbao = new TreeMap<>();
        for (Resource resource : resources) {
            Map<String, Integer> pathCountMap = fenbao.computeIfAbsent(resource.getLvl(), e -> new HashMap<>());
            int slashIndex = resource.getRes().lastIndexOf('/');
            String resDir = resource.getRes().substring(0, slashIndex);
            pathCountMap.compute(resDir, (k, v) -> v == null ? 1 : ++v);
        }

        // 分包等级
        List<LevelFragmemt> lfs = new ArrayList<>();
        if(lvls != null) {
            String[] lvlArr = lvls.split(",");
            for (String lvlStr : lvlArr) {
                Integer lvl = 0;
                Integer minPathCount = 0;
                int colonIndex = lvlStr.indexOf(':');
                if(colonIndex != -1) {
                    lvl = Ints.tryParse(lvlStr.substring(0, colonIndex));
                    minPathCount = Ints.tryParse(lvlStr.substring(colonIndex + 1));
                } else {
                    lvl = Ints.tryParse(lvlStr);
                }
                LevelFragmemt lc = new LevelFragmemt(lvl != null ? lvl : 0, minPathCount != null ? minPathCount : 0);
                lfs.add(lc);
            }
        } else {
            for (Integer lvl : fenbao.keySet()) {
                LevelFragmemt lc = new LevelFragmemt(lvl != null ? lvl : 0, 0);
                lfs.add(lc);
            }
        }
        lfs.sort((o1, o2) -> Integer.compare(o1.getLvl(), o2.getLvl()));

        // 分包统计路径
        int lfIndex = 0;
        for (Map.Entry<Integer, Map<String, Integer>> entry : fenbao.entrySet()) {
            while(lfs.size() > lfIndex + 1 && entry.getKey() >= lfs.get(lfIndex + 1).getLvl()) {
                lfIndex++;
            }
            LevelFragmemt lc = lfs.get(lfIndex);
            for (Map.Entry<String, Integer> lvlEntry : entry.getValue().entrySet()) {
                lc.getPathCountMap().compute(lvlEntry.getKey(), (k, v) -> v == null ? lvlEntry.getValue() : v + lvlEntry.getValue());
            }
            Iterator<Map.Entry<String, Integer>> iterator = lc.getPathCountMap().entrySet().iterator();
            while(iterator.hasNext()) {
                Map.Entry<String, Integer> next = iterator.next();
                if(next.getValue() < lc.getMinPathCount()) {
                    iterator.remove();
                }
            }
        }

        // 将结果levelFragments转成Table，便于前端展示
        List<Integer> lvlHeaderList = lfs.stream().map(e -> e.getLvl()).collect(Collectors.toList());
        int[] lvlHeaderArr = new int[lvlHeaderList.size()];
        for (int i = 0; i < lvlHeaderList.size(); i++) {
            lvlHeaderArr[i] = lvlHeaderList.get(i);
        }
        int maxColumnNum = lfs.stream().mapToInt(e -> e.getPathCountMap().size()).max().getAsInt();
        String[][] content = new String[maxColumnNum][lvlHeaderArr.length];
        for (int i = 0; i < lfs.size(); i++) {
            Map<String, Integer> pathCountMap = lfs.get(i).getPathCountMap();
            List<PathCount> pathCountList = pathCountMap.entrySet().stream().map(e -> new PathCount(e.getKey(), e.getValue())).collect(Collectors.toList());
            pathCountList.sort((o1, o2) -> Integer.compare(o2.getCount(), o1.getCount()));
            for (int j = 0; j < pathCountList.size(); j++) {
                PathCount pathCount = pathCountList.get(j);
                content[j][i] = pathCount.getPath() + ":" + pathCount.getCount();
            }
        }
        FenBaoTable fenBaoTable = new FenBaoTable(lvlHeaderArr, content);
        request.setAttribute("fenBaoTable", fenBaoTable);

        return "fenbao";
    }


}
