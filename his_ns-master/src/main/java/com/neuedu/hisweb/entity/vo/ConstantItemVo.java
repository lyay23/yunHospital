package com.neuedu.hisweb.entity.vo;

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
 * @since 2023-07-26
 */
@Data
public class ConstantItemVo implements Serializable {

    private static final long serialVersionUID = 1L;


    private Integer id;

    /**
     * 所属常数类别ID
     */
    private Integer constantTypeID;

    /**
     * 所属常数类别名称
     */
    private String constantTypeName;
    /**
     * 常数项编码
     */
    private String constantCode;

    /**
     * 常数项名称
     */
    private String constantName;

    /**
     * 删除标记
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Integer delMark;



    @Override
    public String toString() {
        return "ConstantItem{" +
        "id=" + id +
        ", constantTypeID=" + constantTypeID +
        ", constantCode=" + constantCode +
        ", constantName=" + constantName +
        "}";
    }
}
