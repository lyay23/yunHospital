package com.neuedu.hisweb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 
 * </p>
 *
 * @author lynn
 * @since 2023-07-30
 */
@Data
@TableName("Registlevel")
public class Registlevel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID主键
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /**
     * 号别编码
     */
    private String registCode;

    /**
     * 号别名称
     */
    private String registName;

    /**
     * 显示顺序号
     */
    private Integer sequenceNo;

    /**
     * 挂号费
     */
    private BigDecimal registFee;

    /**
     * 挂号限额
     */
    private Integer registQuota;

    /**
     * 删除标记
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @TableLogic
    private Integer delMark;


    @Override
    public String toString() {
        return "Registlevel{" +
        "id=" + id +
        ", registCode=" + registCode +
        ", registName=" + registName +
        ", sequenceNo=" + sequenceNo +
        ", registFee=" + registFee +
        ", registQuota=" + registQuota +
        ", delMark=" + delMark +
        "}";
    }
}
