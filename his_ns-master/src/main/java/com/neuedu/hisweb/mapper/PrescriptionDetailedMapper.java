package com.neuedu.hisweb.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.hisweb.entity.PrescriptionDetailed;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neuedu.hisweb.entity.vo.PrescriptionDetailedVo;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lynn
 * @since 2024-07-26
 */
public interface PrescriptionDetailedMapper extends BaseMapper<PrescriptionDetailed> {

    @Select("SELECT pd.*, d.DrugsName, d.DrugsFormat, d.DrugsPrice " +
            "FROM prescriptiondetailed pd " +
            "JOIN drugs d ON pd.DrugsID = d.id " +
            "WHERE pd.PrescriptionID = #{prescriptionId}")
    @Results({
            @Result(column = "DrugsName", property = "drugsName"),
            @Result(column = "DrugsFormat", property = "drugsFormat"),
            @Result(column = "DrugsPrice", property = "drugsPrice")
    })
    Page<PrescriptionDetailedVo> selectPage(Page<PrescriptionDetailedVo> page, Integer prescriptionId);
} 