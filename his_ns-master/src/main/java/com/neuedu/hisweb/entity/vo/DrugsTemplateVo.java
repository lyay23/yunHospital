package com.neuedu.hisweb.entity.vo;

import com.neuedu.hisweb.entity.DrugsTemplate;
import lombok.Data;

@Data
public class DrugsTemplateVo extends DrugsTemplate {
    private Integer drugsID;
    private String drugsUsage;
    private String dosage;
    private String frequency;
    private String drugsName;
    private String drugsFormat;
    private String drugsUnit;
} 