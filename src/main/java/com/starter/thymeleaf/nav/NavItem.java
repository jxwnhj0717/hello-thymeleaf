package com.starter.thymeleaf.nav;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NavItem {
    private String name;
    private String location;
}
