package com.neuedu.hisweb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author lynn
 * @since 2024-07-27
 */
@Getter
@Setter
@TableName("drugs_detailed")
public class DrugsDetailed implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 成药模板ID
     */
    private Integer drugsTemplatedId;

    /**
     * 药品ID
     */
    private Integer drugsId;

    /**
     * 用法
     */
    private String dosage;

    /**
     * 频次
     */
    private String frequency;
} 