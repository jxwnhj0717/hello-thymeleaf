package com.starter.thymeleaf;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PathCount {
    private String path;
    private int count;
}
