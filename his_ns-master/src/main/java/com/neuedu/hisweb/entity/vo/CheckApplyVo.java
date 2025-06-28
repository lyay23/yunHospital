package com.neuedu.hisweb.entity.vo;

import com.neuedu.hisweb.aop.NoOperationLog;
import com.neuedu.hisweb.entity.CheckApply;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
 
@Data
@EqualsAndHashCode(callSuper = true)
public class CheckApplyVo extends CheckApply {
    private String realName;
    private String deptName;
    private String itemName;
    private String resultDesc;
    private String resultImages;
    private BigDecimal price;
    private BigDecimal totalAmount;
} 