package com.starter.thymeleaf.fenbao;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FenBaoTable {
    private int[] header;
    private String[][] content;
}
