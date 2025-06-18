package com.neuedu.hisweb.entity.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author lynn
 * @since 2023-08-09
 */
@Data
public class CustomerVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String realName;

    private Integer gender;

    private String idnumber;

    private String birthdate;

    private String phone;

    private String createdate;

    private Integer channel;

    private String channelName;

    @Override
    public String toString() {
        return "CustomerVo{" +
                "id=" + id +
                ", realName='" + realName + '\'' +
                ", gender=" + gender +
                ", idnumber='" + idnumber + '\'' +
                ", birthdate='" + birthdate + '\'' +
                ", phone='" + phone + '\'' +
                ", createdate='" + createdate + '\'' +
                ", channel=" + channel +
                ", channelName='" + channelName + '\'' +
                '}';
    }
}
