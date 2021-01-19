package com.starter.thymeleaf.fenbao;

import com.google.common.primitives.Ints;
import com.starter.thymeleaf.res.Resource;
import com.starter.thymeleaf.res.ResourceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Controller
public class FenBaoController {

    @Autowired
    private ResourceRepository resourceRepository;


    @GetMapping("fenbao")
    public String fenbao(HttpServletRequest request,
                         @RequestParam(required = false) String uid,
                         @RequestParam(required = false) String lvls,
                         @RequestParam(required = false) String daterange) throws ParseException {
        // 01/19/2021 - 01/19/2021
        Date startTime = null;
        Date endTime = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            if(StringUtils.hasText(daterange)) {
                String[] split = daterange.split(" - ");
                if(split.length > 0) {
                    startTime = sdf.parse(split[0].trim());
                }
                if(split.length > 1) {
                    endTime = sdf.parse(split[1].trim());
                }
            }
        } catch (Exception e) {
            log.info("", e);
        }

        List<Resource> resources = resourceRepository.getResources(uid, startTime, endTime);

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
        if(StringUtils.hasText(lvls)) {
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
        }

        // 过滤数量不满足的
        for (LevelFragmemt lf : lfs) {
            Iterator<Map.Entry<String, Integer>> iterator = lf.getPathCountMap().entrySet().iterator();
            while(iterator.hasNext()) {
                Map.Entry<String, Integer> next = iterator.next();
                if(next.getValue() < lf.getMinPathCount()) {
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

        request.setAttribute("uid", uid);
        request.setAttribute("lvls", lvls);
        request.setAttribute("daterange", daterange);
        request.setAttribute("fenBaoTable", fenBaoTable);

        return "fenbao";
    }


}
