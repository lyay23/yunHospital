package com.neuedu.hisweb.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.hisweb.entity.MedicalRecord;
import com.neuedu.hisweb.entity.vo.MedicalDiseaseVo;
import com.neuedu.hisweb.entity.vo.MedicalRecordVo;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lynn
 * @since 2023-11-21
 */
public interface MedicalRecordMapper extends BaseMapper<MedicalRecord>   {


    //OneToMany一对多查询
    @Select("""
        select id,CaseNumber,RegistID,Readme,Present,PresentTreat,History,Allergy,
                           Physique,Proposal,Careful,CheckResult,Diagnosis,Handling,CaseState 
                     from MedicalRecord
                    where RegistID=#{rid}
    """)
//    @Results({@Result(column = "id",property = "id"),                         // <1>
//            @Result(column = "CaseNumber",property = "caseNumber"),
//            @Result(column = "RegistID",property = "registID"),
//            @Result(column = "Readme",property = "readme"),
//            @Result(column = "Present",property = "present"),
//            @Result(column = "PresentTreat",property = "presentTreat"),
//            @Result(column = "History",property = "history"),
//            @Result(column = "Allergy",property = "allergy"),
//            @Result(column = "Physique",property = "physique"),
//            @Result(column = "Proposal",property = "proposal"),
//            @Result(column = "Careful",property = "careful"),
//            @Result(column = "CheckResult",property = "checkResult"),
//            @Result(column = "Diagnosis",property = "diagnosis"),
//            @Result(column = "Handling",property = "handling"),
//            @Result(column = "CaseState",property = "caseState"),
//    })
    MedicalRecord getOneByRid(@Param("rid") Integer rid);

    MedicalRecordVo getByRid(Integer rid);

    MedicalRecordVo getMedicalRecordByRegistId(@Param("registId") Integer registId);

    List<MedicalDiseaseVo> getMedicalDiseasesByMedicalId(Integer medicalId);
}
