package com.neuedu.hisweb.entity.vo;

import com.neuedu.hisweb.entity.Prescription;
import lombok.Data;

import java.util.List;

@Data
public class PrescriptionVo extends Prescription {
    private List<Integer> ids;
    private Integer templateId;
    private Integer prescriptionId;
} 