package com.starter.thymeleaf;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class Resource {
    @ExcelIgnore
    private String id;
    @ExcelProperty("uid")
    private String uid;
    @ExcelProperty("等级")
    private int lvl;
    @ExcelProperty("资源")
    private String res;
    @ExcelProperty("资源大小")
    private int resSize;
    @ExcelProperty("开始加载时间")
    private Date startTime;
    @ExcelProperty("结束加载时间")
    private Date endTime;
    @ExcelProperty("持续时间")
    private int dt;
    @ExcelProperty("请求次数")
    private int requestCount;
}
