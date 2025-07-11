package com.neuedu.hisweb.entity.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.neuedu.hisweb.entity.Disease;
import com.neuedu.hisweb.entity.Disecategory;
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
public class DiseaseVo extends Disease {

    private static final long serialVersionUID = 1L;

    /**
     * ID主键
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /**
     * 疾病助记编码
     */
    private String diseaseCode;

    /**
     * 疾病名称
     */
    private String diseaseName;

    /**
     * 国际ICD编码
     */
    private String diseaseICD;

    /**
     * 疾病所属分类
     */
    private Integer diseCategoryID;

    private String diseCategoryName;

    private Disecategory disecategory;


}
