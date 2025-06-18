package com.neuedu.hisweb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * <p>
 * 
 * </p>
 *
 * @author lynn
 * @since 2023-08-09
 */
@Data
@TableName("MedicalCard")
public class MedicalCard implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String realname;

    private Integer gender;

    private String idnumber;

    private String birthdate;

    private String phone;

    private String addr;

    private Integer cardtype;

    private String cardNo;

    private Integer customerId;

    private Integer relationship;

    private String createdate;

    private Integer channel;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @TableLogic
    private Integer delMark;


    @Override
    public String toString() {
        return "Medicalcard{" +
        "id=" + id +
        ", realname=" + realname +
        ", gender=" + gender +
        ", idnumber=" + idnumber +
        ", birthdate=" + birthdate +
        ", phone=" + phone +
        ", cardtype=" + cardtype +
        ", cardNo=" + cardNo +
        ", customerId=" + customerId +
        ", relationship=" + relationship +
        ", createdate=" + createdate +
        ", channel=" + channel +
        ", delMark=" + delMark +
        "}";
    }
}
