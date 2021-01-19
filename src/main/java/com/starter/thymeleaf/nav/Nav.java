package com.starter.thymeleaf.nav;

import lombok.Data;

import java.util.List;

@Data
public class Nav {
    private NavItem current;
    List<NavItem> items;
}
