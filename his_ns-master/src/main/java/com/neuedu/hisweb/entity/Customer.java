package com.neuedu.hisweb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author lynn
 * @since 2023-08-09
 */
@Data
@TableName("customer")
public class Customer implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String realName;

    private Integer gender;

    private String idnumber;

    private String birthdate;

    private String phone;

    private Date createdate;

    private Integer channel;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @TableLogic
    private Integer delMark;


    @Override
    public String toString() {
        return "Customer{" +
        "id=" + id +
        ", realName=" + realName +
        ", gender=" + gender +
        ", idnumber=" + idnumber +
        ", birthdate=" + birthdate +
        ", phone=" + phone +
        ", createdate=" + createdate +
        ", channel=" + channel +
        ", delMark=" + delMark +
        "}";
    }
}
