package com.starter.thymeleaf;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class ResourceRepository {

    private Map<String, Resource> resourceMap = new HashMap<>();

    @PostConstruct
    private void init() {

        for (int i = 0; i < 10000; i++) {
            Resource res = Resource.builder()
                    .id(UUID.randomUUID().toString())
                    .uid("hj")
                    .lvl(ThreadLocalRandom.current().nextInt(10) + 1)
                    .res("/res/" + UUID.randomUUID().toString() + ".png")
                    .resSize(1000)
                    .startTime(new Date(System.currentTimeMillis() - ThreadLocalRandom.current().nextInt(10000)))
                    .endTime(new Date(System.currentTimeMillis() + ThreadLocalRandom.current().nextInt(10000)))
                    .requestCount(1)
                    .build();
            res.setDt((int) (res.getEndTime().getTime() - res.getStartTime().getTime()));
            resourceMap.put(res.getId(), res);
        }


        for (int i = 0; i < 10000; i++) {
            Resource res = Resource.builder()
                    .id(UUID.randomUUID().toString())
                    .uid("hj2")
                    .lvl(ThreadLocalRandom.current().nextInt(10) + 1)
                    .res("/js/" + UUID.randomUUID().toString() + ".js")
                    .resSize(1000)
                    .startTime(new Date(System.currentTimeMillis() - ThreadLocalRandom.current().nextInt(10000)))
                    .endTime(new Date(System.currentTimeMillis() + ThreadLocalRandom.current().nextInt(10000)))
                    .requestCount(1)
                    .build();
            res.setDt((int) (res.getEndTime().getTime() - res.getStartTime().getTime()));
            resourceMap.put(res.getId(), res);
        }
    }

    public List<Resource> getResources(String uid) {
        List<Resource> resources = null;
        if(!StringUtils.hasText(uid)) {
            resources = resourceMap.values().stream().collect(Collectors.toList());
        } else {
            resources = resourceMap.values().stream().filter(e -> e.getUid().equals(uid)).collect(Collectors.toList());
        }
        if(resources == null) {
            resources = Collections.emptyList();
        }
        return resources;
    }
}
