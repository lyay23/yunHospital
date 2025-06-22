package com.neuedu.hisweb.entity;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.io.Serializable;

/**
 * <p>
 * </p>
 *
 * @author lynn
 * @since 2023-07-17
 */
@Data
@TableName("user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID主键
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;
    /**
     * 登录名
     */
    private String userName;
    /**
     * 密码
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    /**
     * 真实姓名
     */
    private String realName;
    /**
     * 用户类别
     */
    private Integer useType;

    /**
     * 医生职称ID
     */
    private Integer docTitleID;

    /**
     * 是否参与排班
     */
    private String isScheduling;

    /**
     * 所在科室ID
     */
    private Integer deptID;

    /**
     * 挂号级别ID
     */
    private Integer registLeID;

    /**
     * 删除标记
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @TableLogic
    private Integer delMark;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Integer db;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
