package com.neuedu.hisweb.entity.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
public class RuleVo implements Serializable {

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

    private String deptName;
    /**
     * 医生ID
     */
    private Integer userID;

    private String realName;
    /**
     * 星期
     */
    private String week;

    private Integer num;

    @Override
    public String toString() {
        return "RuleVo{" +
                "id=" + id +
                ", ruleName='" + ruleName + '\'' +
                ", deptID=" + deptID +
                ", deptName='" + deptName + '\'' +
                ", userID=" + userID +
                ", realName='" + realName + '\'' +
                ", week='" + week + '\'' +
                ", num=" + num +
                '}';
    }
}
