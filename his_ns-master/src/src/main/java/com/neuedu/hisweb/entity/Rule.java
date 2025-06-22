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
@TableName("Rule")
public class Rule implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID主键
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /**
     * 规则名称
     */
    private String ruleName;

    /**
     * 科室ID
     */
    private Integer deptID;

    /**
     * 医生ID
     */
    private Integer userID;

    /**
     * 星期
     */
    private String week;

    private Integer num;

    /**
     * 删除标记
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @TableLogic
    private Integer delMark;


    @Override
    public String toString() {
        return "Rule{" +
        "id=" + id +
        ", ruleName=" + ruleName +
        ", deptID=" + deptID +
        ", userID=" + userID +
        ", week=" + week +
        ", num=" + num +
        ", delMark=" + delMark +
        "}";
    }
}
