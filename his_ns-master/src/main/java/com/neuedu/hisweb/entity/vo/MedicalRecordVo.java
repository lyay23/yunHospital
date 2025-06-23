package com.neuedu.hisweb.entity.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.neuedu.hisweb.entity.MedicalDisease;
import com.neuedu.hisweb.entity.MedicalRecord;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 
 * </p>
 *
 * @author lynn
 * @since 2023-11-21
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MedicalRecordVo extends MedicalRecord implements Serializable {

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
     * 挂号ID
     */
    private Integer registId;

    /**
     * 主诉
     */
    private String readme;

    /**
     * 现病史
     */
    private String present;

    /**
     * 现病治疗情况
     */
    private String presentTreat;

    /**
     * 既往史
     */
    private String history;

    /**
     * 过敏史
     */
    private String allergy;

    /**
     * 体格检查
     */
    private String physique;

    /**
     * 检查建议
     */
    private String proposal;

    /**
     * 注意事项
     */
    private String careful;

    /**
     * 检查结果
     */
    private String checkResult;

    /**
     * 诊断结果
     */
    private String diagnosis;

    /**
     * 处理意见
     */
    private String handling;

    /**
     * 病历状态 1-暂存 2-已提交 3-诊毕
     */
    private Integer caseState;

    private List<MedicalDiseaseVo> medicalDiseases;

    @Override
    public String toString() {
        return "Medicalrecord{" +
        "id=" + id +
        ", caseNumber=" + caseNumber +
        ", registId=" + registId +
        ", readme=" + readme +
        ", present=" + present +
        ", presentTreat=" + presentTreat +
        ", history=" + history +
        ", allergy=" + allergy +
        ", physique=" + physique +
        ", proposal=" + proposal +
        ", careful=" + careful +
        ", checkResult=" + checkResult +
        ", diagnosis=" + diagnosis +
        ", handling=" + handling +
        ", caseState=" + caseState +
        "}";
    }
}
