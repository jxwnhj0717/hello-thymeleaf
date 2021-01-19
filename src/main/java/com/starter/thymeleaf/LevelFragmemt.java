package com.starter.thymeleaf;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class LevelFragmemt {
    private int lvl;
    private int minPathCount;
    private Map<String, Integer> pathCountMap;

    public LevelFragmemt(int lvl, int minPathCount) {
        this.lvl = lvl;
        this.minPathCount = minPathCount;
        this.pathCountMap = new HashMap<>();
    }
}
