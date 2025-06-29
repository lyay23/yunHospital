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
    private String patientName;
    private String caseNumber;
    private Integer age;
    private String sex;
    private String itemName;
    private String deptName;
    private String doctorName;
    private String settleCategoryName;
    private String invoiceNo;
    private String resultDesc;
    private String resultImages;
    private BigDecimal price;
    private BigDecimal totalAmount;
    private String startTime;
    private String endTime;
} 