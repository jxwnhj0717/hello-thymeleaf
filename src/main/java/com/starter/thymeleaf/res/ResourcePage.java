package com.starter.thymeleaf.res;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ResourcePage {
    private int total;
    private List<Resource> rows;
}
