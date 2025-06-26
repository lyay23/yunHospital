package com.neuedu.hisweb.entity.vo;

import com.neuedu.hisweb.entity.Patientcosts;
import lombok.Data;

@Data
public class PatientCostVo extends Patientcosts {
    private String patientName;
    private String caseNumber;
    private String spec; //规格
    private Integer dosage; //付数
    private String status; //状态
} 