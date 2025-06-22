package com.neuedu.hisweb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author lynn
 * @since 2023-08-09
 */
@Data
@TableName("Invoice")
public class Invoice implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID主键
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /**
     * 发票号码
     */
    private String invoiceNum;

    /**
     * 发票金额
     */
    private double money;

    /**
     * 发票状态  1-正常  2-作废 
     */
    private Integer state;

    /**
     * 收/退费时间 
     */
    private String creationTime;

    /**
     * 收/退费人员ID 
     */
    private Integer userID;

    /**
     * 挂号ID 
     */
    private Integer registID;

    /**
     * 收费方式 
     */
    private Integer feeType;

    /**
     * 冲红发票号码
     */
    private String back;

    /**
     * 发票状态  0-未日结  1-已提交  2-已审核 
     */
    private Integer dailyState;



    @Override
    public String toString() {
        return "Invoice{" +
        "id=" + id +
        ", invoiceNum=" + invoiceNum +
        ", money=" + money +
        ", state=" + state +
        ", creationTime=" + creationTime +
        ", userID=" + userID +
        ", registID=" + registID +
        ", feeType=" + feeType +
        ", back=" + back +
        ", dailyState=" + dailyState +
        "}";
    }
}
