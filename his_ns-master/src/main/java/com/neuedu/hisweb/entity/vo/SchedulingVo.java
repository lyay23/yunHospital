package com.neuedu.hisweb.entity.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * <p>
 * 
 * </p>
 *
 * @author lynn
 * @since 2023-07-31
 */
@Data
public class SchedulingVo implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * ID主键
     */
    private Integer id;
    /**
     * 排班日期
     */
    private LocalDate schedDate;
    /**
     * 科室ID
     */
    private Integer deptID;

    private String deptName;
    /**
     * 医生ID
     */
    private Integer userID;

    private String realName;

    private Integer registlevelId;

    private String registName;

    private double registFee;

    private Integer registQuota;

    /**
     * 午别
     */
    private String noon;

    /**
     * 排班规则ID
     */
    private Integer ruleID;

    private String ruleName;
    /**
     * 已预约挂号人数
     */
    private Integer regNum;

    @Override
    public String toString() {
        return "SchedulingVo{" +
                "id=" + id +
                ", schedDate=" + schedDate +
                ", deptID=" + deptID +
                ", deptName='" + deptName + '\'' +
                ", userID=" + userID +
                ", realName='" + realName + '\'' +
                ", registName='" + registName + '\'' +
                ", registFee=" + registFee +
                ", registQuota=" + registQuota +
                ", noon='" + noon + '\'' +
                ", ruleID=" + ruleID +
                ", ruleName='" + ruleName + '\'' +
                '}';
    }
}
