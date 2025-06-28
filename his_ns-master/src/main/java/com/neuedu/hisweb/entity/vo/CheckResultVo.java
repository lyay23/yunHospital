package com.neuedu.hisweb.entity.vo;

import lombok.Data;

import java.util.List;

@Data
public class CheckResultVo {
    private List<Integer> checkApplyIds;
    private Integer registId;
    private String resultDesc;
    private String resultImages; // 逗号分隔的URL字符串
} 