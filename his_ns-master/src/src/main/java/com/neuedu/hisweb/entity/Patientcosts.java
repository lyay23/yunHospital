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
@TableName("Patientcosts")
public class Patientcosts implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID主键
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /**
     * 挂号ID
     */
    private Integer registID;

    /**
     * 发票ID
     */
    private Integer invoiceID;

    /**
     * 项目ID
     */
    private Integer itemID;

    /**
     * 项目类型 1-非药品 2-药品
     */
    private Integer itemType;

    /**
     * 项目名称
     */
    private String name;

    /**
     * 项目单价
     */
    private double price;

    /**
     * 数量
     */
    private double amount;

    /**
     * 执行科室ID
     */
    private Integer deptID;

    /**
     * 开立时间
     */
    private String createtime;

    /**
     * 开立人员ID
     */
    private Integer createOperID;

    /**
     * 收/退费时间
     */
    private String payTime;

    /**
     * 收/退费人员ID
     */
    private Integer registerID;

    /**
     * 收费方式
     */
    private Integer feeType;

    /**
     * 退费对应记录ID
     */
    private Integer backID;


    @Override
    public String toString() {
        return "Patientcosts{" +
        "id=" + id +
        ", registID=" + registID +
        ", invoiceID=" + invoiceID +
        ", itemID=" + itemID +
        ", itemType=" + itemType +
        ", name=" + name +
        ", price=" + price +
        ", amount=" + amount +
        ", deptID=" + deptID +
        ", createtime=" + createtime +
        ", createOperID=" + createOperID +
        ", payTime=" + payTime +
        ", registerID=" + registerID +
        ", feeType=" + feeType +
        ", backID=" + backID +
        "}";
    }
}
