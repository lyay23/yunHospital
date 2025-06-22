package com.neuedu.hisweb.entity.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
public class MedicalCardVo implements Serializable {

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

    private String cardtypeName;

    private String cardNo;

    private Integer customerId;

    private String customerName;

    private Integer relationship;

    private String  relationshipName;

    private String createdate;

    private Integer channel;

    private String channelName;

    @Override
    public String toString() {
        return "MedicalCardVo{" +
                "id=" + id +
                ", realname='" + realname + '\'' +
                ", gender=" + gender +
                ", idnumber='" + idnumber + '\'' +
                ", birthdate='" + birthdate + '\'' +
                ", phone='" + phone + '\'' +
                ", cardtype=" + cardtype +
                ", cardtypeName=" + cardtypeName +
                ", cardNo='" + cardNo + '\'' +
                ", customerId=" + customerId +
                ", customerName='" + customerName + '\'' +
                ", relationship=" + relationship +
                ", relationshipName='" + relationshipName + '\'' +
                ", createdate='" + createdate + '\'' +
                ", channel=" + channel +
                ", channelName='" + channelName + '\'' +
                '}';
    }
}
