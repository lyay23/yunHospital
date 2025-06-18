package com.neuedu.hisweb.entity.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author lynn
 * @since 2023-07-31
 */
@Data
@TableName("Fmeditem")
public class FmeditemVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID主键
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /**
     * 项目编码
     */
    private String itemCode;

    /**
     * 项目名称
     */
    private String itemName;

    /**
     * 规格
     */
    private String format;

    /**
     * 单价
     */
    private BigDecimal price;

    /**
     * 所属费用科目ID
     */
    private Integer expClassID;

    /**
     * 所属费用科目ID
     */
    private String expName;

    /**
     * 执行科室ID
     */
    private Integer deptID;

    /**
     * 执行科室ID
     */
    private String deptName;

    /**
     * 拼音助记码
     */
    private String mnemonicCode;

    /**
     * 创建时间
     */
    private LocalDateTime creationDate;

    /**
     * 最后修改时间
     */
    private LocalDateTime lastUpdateDate;

    /**
     * 项目类型
     */
    private Integer recordType;

    private String recordTypeName;

    @Override
    public String toString() {
        return "FmeditemVo{" +
                "id=" + id +
                ", itemCode='" + itemCode + '\'' +
                ", itemName='" + itemName + '\'' +
                ", format='" + format + '\'' +
                ", price=" + price +
                ", expClassID=" + expClassID +
                ", expName='" + expName + '\'' +
                ", deptID=" + deptID +
                ", deptName='" + deptName + '\'' +
                ", mnemonicCode='" + mnemonicCode + '\'' +
                ", creationDate=" + creationDate +
                ", lastUpdateDate=" + lastUpdateDate +
                ", recordType=" + recordType +
                ", recordTypeName='" + recordTypeName + '\'' +
                '}';
    }
}
