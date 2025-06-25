package com.neuedu.hisweb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author lynn
 * @since 2024-07-26
 */
@Getter
@Setter
@TableName("prescription")
public class Prescription implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 病历ID
     */
    @TableField("MedicalID")
    private Integer medicalId;

    /**
     * 挂号ID
     */
    @TableField("RegistID")
    private Integer registId;

    /**
     * 开立医生ID
     */
    @TableField("UserID")
    private Integer userId;

    /**
     * 处方名称
     */
    private String prescriptionName;

    /**
     * 开立时间
     */
    private LocalDateTime prescriptionTime;

    /**
     * 处方状态 1-暂存 2-已开立 0-已作废
     */
    private Integer prescriptionState;
} 