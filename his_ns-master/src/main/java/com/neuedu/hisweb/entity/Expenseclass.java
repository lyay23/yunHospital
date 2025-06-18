package com.neuedu.hisweb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author lynn
 * @since 2023-08-01
 */
@Data
@TableName("Expenseclass")
public class Expenseclass implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID主键
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /**
     * 费用科目编码
     */
    private String expCode;

    /**
     * 费用科目名称
     */
    private String expName;

    /**
     * 删除标记
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @TableLogic
    private Integer delMark;


    @Override
    public String toString() {
        return "Expenseclass{" +
        "id=" + id +
        ", expCode=" + expCode +
        ", expName=" + expName +
        ", delMark=" + delMark +
        "}";
    }
}
