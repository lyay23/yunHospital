package com.neuedu.hisweb.entity.vo;

import com.neuedu.hisweb.entity.PrescriptionDetailed;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PrescriptionDetailedVo extends PrescriptionDetailed {

    private String drugsName;

    private String drugsFormat;

    private BigDecimal drugsPrice;
} 