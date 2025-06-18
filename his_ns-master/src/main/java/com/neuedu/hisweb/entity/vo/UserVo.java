package com.neuedu.hisweb.entity.vo;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.neuedu.hisweb.entity.enums.UserTypeEnum;
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
public class UserVo implements Serializable {

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

    private String useTypeName;
    /**
     * 医生职称ID
     */
    private Integer docTitleID;
    /**
     * 医生职称
     */
    private String docTitle;

    /**
     * 是否参与排班
     */
    private String isScheduling;

    /**
     * 所在科室ID
     */
    private Integer deptID;

    /**
     * 所在科室
     */
    private String dept;

    /**
     * 挂号级别ID
     */
    private Integer registLeID;

    /**
     * 挂号级别
     */
    private String registLe;


    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
