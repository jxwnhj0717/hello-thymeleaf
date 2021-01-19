package com.starter.thymeleaf;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FenBaoTable {
    private int[] header;
    private String[][] content;
}
