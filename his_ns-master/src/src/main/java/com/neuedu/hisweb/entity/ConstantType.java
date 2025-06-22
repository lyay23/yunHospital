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
 * @since 2023-07-25
 */
@Data
@TableName("constanttype")
public class ConstantType implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID主键
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /**
     * 常数类别编码
     */
    private String constantTypeCode;

    /**
     * 常数类别名称
     */
    private String constantTypeName;

    /**
     * 删除标记
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @TableLogic
    private Integer delMark;


    @Override
    public String toString() {
        return "Constanttype{" +
        "id=" + id +
        ", constantTypeCode=" + constantTypeCode +
        ", constantTypeName=" + constantTypeName +
        ", delMark=" + delMark +
        "}";
    }
}
