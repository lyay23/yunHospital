package com.neuedu.hisweb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
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
@TableName("prescriptiondetailed")
public class PrescriptionDetailed implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 成药处方ID
     */
    private Integer prescriptionId;

    /**
     * 药品ID
     */
    private Integer drugsId;

    /**
     * 药品用法
     */
    private String drugsUsage;

    /**
     * 用量
     */
    private String dosage;

    /**
     * 频次
     */
    private String frequency;

    /**
    * 数量
    */
    private BigDecimal amount;

    /**
     * 状态 1-暂存 2-已开立 3-已交费 4-已发药 5-已退药 6-已退费 0-已作废
     */
    private Integer state;
} 