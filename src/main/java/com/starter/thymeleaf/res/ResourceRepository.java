package com.starter.thymeleaf.res;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Component
public class ResourceRepository {

    private Map<String, Resource> resourceMap = new HashMap<>();

    @PostConstruct
    private void init() {

        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < 10000; i++) {
            Date startTime = new Date(System.currentTimeMillis() - ThreadLocalRandom.current().nextLong(1_000_000_000L));
            Date endTime = new Date(startTime.getTime() + Duration.ofMinutes(3).toMillis());
            Resource res = Resource.builder()
                    .id(UUID.randomUUID().toString())
                    .uid("hj")
                    .lvl(random.nextInt(100) + 1)
                    .res("/image" + random.nextInt(5) + "/" + UUID.randomUUID().toString() + ".png")
                    .resSize(1000)
                    .startTime(startTime)
                    .endTime(endTime)
                    .requestCount(1)
                    .build();
            res.setDt((int) (res.getEndTime().getTime() - res.getStartTime().getTime()));
            resourceMap.put(res.getId(), res);
        }


        for (int i = 0; i < 10000; i++) {
            Date startTime = new Date(System.currentTimeMillis() - ThreadLocalRandom.current().nextLong(1_000_000_000L));
            Date endTime = new Date(startTime.getTime() + Duration.ofMinutes(3).toMillis());
            Resource res = Resource.builder()
                    .id(UUID.randomUUID().toString())
                    .uid("hj2")
                    .lvl(ThreadLocalRandom.current().nextInt(50) + 1)
                    .res("/js" + random.nextInt(5) + "/" + UUID.randomUUID().toString() + ".js")
                    .resSize(1000)
                    .startTime(startTime)
                    .endTime(endTime)
                    .requestCount(1)
                    .build();
            res.setDt((int) (res.getEndTime().getTime() - res.getStartTime().getTime()));
            resourceMap.put(res.getId(), res);
        }
    }

    public List<Resource> getResources(String uid, Date startTime, Date endTime) {
        List<Resource> resources =  resourceMap.values().stream().filter(e -> {
                if(StringUtils.hasText(uid) && !e.getUid().equals(uid)) {
                    return false;
                }
                if(startTime != null && e.getStartTime().compareTo(startTime) < 0) {
                    return false;
                }
                if(endTime != null && e.getEndTime().compareTo(endTime) > 0) {
                    return false;
                }
                return true;
            }).collect(Collectors.toList());
        if(resources == null) {
            resources = Collections.emptyList();
        }
        return resources;
    }
}
