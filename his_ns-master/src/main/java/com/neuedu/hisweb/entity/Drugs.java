package com.neuedu.hisweb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("drugs")
public class Drugs implements Serializable {
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;
    @TableField("DrugsCode")
    private String drugsCode;
    @TableField("DrugsName")
    private String drugsName;
    @TableField("DrugsFormat")
    private String drugsFormat;
    @TableField("DrugsUnit")
    private String drugsUnit;
    @TableField("Manufacturer")
    private String manufacturer;
    @TableField("DrugsDosageID")
    private Integer drugsDosageId;
    @TableField("DrugsTypeID")
    private Integer drugsTypeId;
    @TableField("DrugsPrice")
    private BigDecimal drugsPrice;
    @TableField("MnemonicCode")
    private String mnemonicCode;
    @TableField("CreationDate")
    private LocalDateTime creationDate;
    @TableField("DelMark")
    private Integer delmark;
} 