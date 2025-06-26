package com.neuedu.hisweb.entity.vo;

import com.neuedu.hisweb.entity.CheckApply;
import lombok.Data;
import java.math.BigDecimal;

 
@Data
public class CheckApplyVo extends CheckApply {
    private String deptName;
    private BigDecimal price;
} 