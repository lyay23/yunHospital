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
 * @since 2023-07-31
 */
@Data
@TableName("Disecategory")
public class Disecategory implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID主键
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /**
     * 分类编码
     */
    private String dicaCode;

    /**
     * 分类名称
     */
    private String dicaName;

    /**
     * 显示顺序号
     */
    private Integer sequenceNo;

    /**
     * 疾病类型
     */
    private Integer dicaType;

    /**
     * 删除标记
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @TableLogic
    private Integer delMark;


    @Override
    public String toString() {
        return "Disecategory{" +
        "id=" + id +
        ", dicaCode=" + dicaCode +
        ", dicaName=" + dicaName +
        ", sequenceNo=" + sequenceNo +
        ", dicaType=" + dicaType +
        ", delMark=" + delMark +
        "}";
    }
}
