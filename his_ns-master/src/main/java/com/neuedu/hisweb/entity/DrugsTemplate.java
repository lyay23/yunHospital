package com.neuedu.hisweb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * <p>
 * 
 * </p>
 *
 * @author lynn
 * @since 2024-07-27
 */
@Data
@TableName("DrugsTemplate")
public class DrugsTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /**
     * 名称
     */
    private String name;

    /**
     * 医生ID
     */
    private Integer userId;

    /**
     * 创建时间
     */
    private LocalDateTime creationTime;

    /**
     * 使用范围 1-个人 2-科室 3-全院
     */
    private String scope;

    /**
     * 删除标记 1-正常 0-删除
     */
    private Integer delmark;
} 