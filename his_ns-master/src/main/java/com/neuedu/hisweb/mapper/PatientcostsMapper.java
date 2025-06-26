package com.neuedu.hisweb.mapper;

import com.neuedu.hisweb.entity.Patientcosts;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.neuedu.hisweb.entity.vo.PatientCostVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lynn
 * @since 2023-08-09
 */
@Mapper
public interface PatientcostsMapper extends BaseMapper<Patientcosts> {
    List<PatientCostVo> selectPatientCost(@Param("keyword") String keyword, @Param("itemType") Integer itemType);
}
