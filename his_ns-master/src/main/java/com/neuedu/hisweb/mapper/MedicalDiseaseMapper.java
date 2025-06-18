package com.neuedu.hisweb.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neuedu.hisweb.entity.MedicalDisease;
import com.neuedu.hisweb.entity.vo.MedicalDiseaseVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lynn
 * @since 2023-11-21
 */
@Mapper
public interface MedicalDiseaseMapper extends BaseMapper<MedicalDisease> {

    @Select("""
        select m.id,m.MedicalId,m.RegistId,m.DiseaseId,d.DiseaseCode,d.DiseaseICD,
                d.DiseaseName,m.DiagnoseType,m.GetSiskDate,m.DiagnoseCate
        from MedicalDisease m
        left join Disease d on m.DiseaseId=d.id
        where m.RegistId=#{rid} and m.DiagnoseType=#{type}
    """)
    public List<MedicalDiseaseVo> getListByRid(@Param("rid") Integer rid, @Param("type") Integer type);
}
