package com.neuedu.hisweb.entity;

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
public class Fmeditem implements Serializable {

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
     * 执行科室ID
     */
    private Integer deptID;

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

    /**
     * 删除标记
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @TableLogic
    private Integer delMark;

    @Override
    public String toString() {
        return "Fmeditem{" +
        "id=" + id +
        ", itemCode=" + itemCode +
        ", itemName=" + itemName +
        ", format=" + format +
        ", price=" + price +
        ", expClassID=" + expClassID +
        ", deptID=" + deptID +
        ", mnemonicCode=" + mnemonicCode +
        ", creationDate=" + creationDate +
        ", lastUpdateDate=" + lastUpdateDate +
        ", recordType=" + recordType +
        ", delMark=" + delMark +
        "}";
    }
}
