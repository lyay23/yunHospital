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
 * @since 2023-07-29
 */
@Data
@TableName("department")
public class Department implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID主键
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /**
     * 科室编码
     */
    private String deptCode;

    /**
     * 科室名称
     */
    private String deptName;

    /**
     * 科室分类
     */
    private Integer deptCategoryID;

    /**
     * 科室类型
     */
    private Integer deptType;

    /**
     * 删除标记
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @TableLogic
    private Integer delMark;




    @Override
    public String toString() {
        return "Department{" +
        "id=" + id +
        ", deptCode=" + deptCode +
        ", deptName=" + deptName +
        ", deptCategoryID=" + deptCategoryID +
        ", deptType=" + deptType +
        ", delMark=" + delMark +
        "}";
    }
}
