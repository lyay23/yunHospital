package com.neuedu.hisweb.entity.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.neuedu.hisweb.entity.Disease;
import com.neuedu.hisweb.entity.MedicalDisease;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
public class MedicalDiseaseVo extends MedicalDisease implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    private Integer medicalID;

    private Integer registID;

    private Integer diseaseID;

    private String diseaseCode;

    private String diseaseName;

    private String diseaseICD;

    private Integer diagnoseType;

    private Integer diagnoseCate;

    private Disease disease;

    private Integer diseCategoryID;
    }