package com.neuedu.hisweb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("CheckApply")
public class CheckApply implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    private Integer medicalId;

    private Integer registId;

    private Integer itemId;

    @TableField(exist = false)
    private String itemName;

    @TableField(exist = false)
    private Integer deptId;

    private String name;

    private String objective;

    private String position;

    private Integer isUrgent;

    private Integer num;

    @TableField("CreationTime")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private LocalDateTime creationTime;

    private Integer doctorId;

    private Integer checkOperId;

    private Integer resultOperId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime checkTime;

    private String result;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime resultTime;

    private Integer state;

    private Integer recordType;


}