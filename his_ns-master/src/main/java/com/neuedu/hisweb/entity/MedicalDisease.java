package com.neuedu.hisweb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author lynn
 * @since 2023-08-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("medicaldisease")
public class MedicalDisease implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID主键
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /**
     * 病历ID
     */
    @TableField("MedicalID")
    @JsonProperty("medicalId")
    private Integer medicalID;

    /**
     * 挂号ID
     */
    @TableField("RegistID")
    @JsonProperty("registId")
    private Integer registID;

    /**
     * 疾病ID
     */
    @TableField("DiseaseID")
    @JsonProperty("diseaseId")
    private Integer diseaseID;

    /**
     * 诊断类型 1-初诊  2-终诊
     */
    @TableField("DiagnoseType")
    private Integer diagnoseType;

    /**
     * 发病日期
     */
    @TableField("GetSiskDate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date siskDate;

    /**
     * 诊断种类 1-待确诊 2-确诊 3-疑似
     */
    @TableField("DiagnoseCate")
    private Integer diagnoseCate;


    @Override
    public String toString() {
        return "Medicaldisease{" +
        "id=" + id +
        ", medicalID=" + medicalID +
        ", registID=" + registID +
        ", diseaseID=" + diseaseID +
        ", diagnoseType=" + diagnoseType +
        ", siskDate=" + siskDate +
        ", diagnoseCate=" + diagnoseCate +
        "}";
    }
}
