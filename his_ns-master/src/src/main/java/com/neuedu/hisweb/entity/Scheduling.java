package com.neuedu.hisweb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author lynn
 * @since 2023-07-31
 */
@Data
@TableName("Scheduling")
public class Scheduling implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID主键
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /**
     * 排班日期
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    private Date schedDate;

    /**
     * 科室ID
     */
    private Integer deptID;

    /**
     * 医生ID
     */
    private Integer userID;

    /**
     * 午别
     */
    private String noon;

    /**
     * 排班规则ID
     */
    private Integer ruleID;

    /**
     * 已预约挂号人数
     */
    private Integer regNum;

    /**
     * 删除标记
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @TableLogic
    private Integer delMark;



    @Override
    public String toString() {
        return "Scheduling{" +
        "id=" + id +
        ", schedDate=" + schedDate +
        ", deptID=" + deptID +
        ", userID=" + userID +
        ", noon=" + noon +
        ", ruleID=" + ruleID +
        ", regNum=" + regNum +
        ", delMark=" + delMark +
        "}";
    }
}
