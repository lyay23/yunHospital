package com.neuedu.hisweb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
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
@TableName("Register")
public class Register implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID主键
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /**
     * 病历号
     */
    private String caseNumber;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 年龄类型 岁 月 天
     */
    private String ageType;

    /**
     * 本次看诊日期
     */
    private String visitDate;

    /**
     * 午别  上午/下午
     */
    private String noon;

    /**
     * 本次挂号科室ID
     */
    private Integer deptID;

    /**
     * 本次挂号医生ID
     */
    private Integer userID;

    /**
     * 本次挂号级别ID
     */
    private Integer registLeID;

    /**
     * 结算类别ID
     */
    private Integer settleID;

    /**
     * 病历本要否  是/否
     */
    private String isBook;

    /**
     * 挂号时间
     */
    private String registTime;

    /**
     * 挂号员ID
     */
    private Integer registerID;

    /**
     * 本次看诊状态
     */
    private Integer visitState;

    private Integer medicalCardId;

    private Integer timeInterval;

    /**
     * 1-	窗口	2-	微信小程序	3-	AndroidApp	4-	IOS	
     */
    private Integer channel;


    @Override
    public String toString() {
        return "Register{" +
        "id=" + id +
        ", caseNumber=" + caseNumber +
        ", age=" + age +
        ", ageType=" + ageType +
        ", visitDate=" + visitDate +
        ", noon=" + noon +
        ", deptID=" + deptID +
        ", userID=" + userID +
        ", registLeID=" + registLeID +
        ", settleID=" + settleID +
        ", isBook=" + isBook +
        ", registTime=" + registTime +
        ", registerID=" + registerID +
        ", visitState=" + visitState +
        ", medicalCardId=" + medicalCardId +
        ", timeInterval=" + timeInterval +
        ", channel=" + channel +
        "}";
    }
}
