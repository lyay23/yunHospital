package com.neuedu.hisweb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author lynn
 * @since 2023-11-21
 */
@Data
@TableName("MedicalDisease")
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
    private Integer medicalID;

    /**
     * 挂号ID
     */
    private Integer registID;

    /**
     * 疾病ID
     */
    private Integer diseaseID;

    /**
     * 诊断类型 1-西医 2-中医
     */
    private Integer diagnoseType;

    /**
     * 发病日期
     */
    private LocalDateTime getSiskDate;

    /**
     * 诊断种类 1-初诊 2-终诊
     */
    private Integer diagnoseCate;


    @Override
    public String toString() {
        return "Medicaldisease{" +
        "id=" + id +
        ", medicalID=" + medicalID +
        ", registID=" + registID +
        ", diseaseID=" + diseaseID +
        ", diagnoseType=" + diagnoseType +
        ", getSiskDate=" + getSiskDate +
        ", diagnoseCate=" + diagnoseCate +
        "}";
    }
}
