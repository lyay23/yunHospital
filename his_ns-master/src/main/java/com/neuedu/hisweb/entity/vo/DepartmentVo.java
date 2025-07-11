package com.neuedu.hisweb.entity.vo;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import com.neuedu.hisweb.entity.enums.*;

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
public class DepartmentVo implements Serializable {

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
     * 科室分类
     */
    private String deptCategoryName;

    /**
     * 科室类型
     */
    private Integer deptType;

    private String deptTypeName;



}
