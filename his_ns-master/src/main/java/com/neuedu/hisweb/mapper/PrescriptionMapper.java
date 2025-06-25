package com.neuedu.hisweb.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.hisweb.entity.Prescription;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neuedu.hisweb.entity.vo.PrescriptionVo;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lynn
 * @since 2024-07-26
 */
public interface PrescriptionMapper extends BaseMapper<Prescription> {

    @Select("select * from prescription where RegistID = #{registId} order by prescriptionTime desc")
    Page<PrescriptionVo> selectPage(Page<PrescriptionVo> page, Integer registId);
} 