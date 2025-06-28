package com.neuedu.hisweb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("medical_result")
public class MedicalResult {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("regist_id")
    private Integer registId;

    @TableField("item_id")
    private Integer itemId;

    @TableField("item_type")
    private Integer itemType;

    @TableField("result_desc")
    private String resultDesc;

    @TableField("result_images")
    private String resultImages;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("operator_id")
    private Integer operatorId;

    @TableField("del_mark")
    private Integer delMark;
} 