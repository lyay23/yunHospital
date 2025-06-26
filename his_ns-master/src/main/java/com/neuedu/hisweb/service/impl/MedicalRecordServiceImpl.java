package com.neuedu.hisweb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.neuedu.hisweb.entity.*;
import com.neuedu.hisweb.entity.vo.MedicalDiseaseVo;
import com.neuedu.hisweb.entity.vo.MedicalRecordVo;
import com.neuedu.hisweb.mapper.MedicalCardMapper;
import com.neuedu.hisweb.mapper.MedicalDiseaseMapper;
import com.neuedu.hisweb.mapper.MedicalRecordMapper;
import com.neuedu.hisweb.mapper.RegisterMapper;
import com.neuedu.hisweb.service.IMedicalCardService;
import com.neuedu.hisweb.service.IMedicalRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lituai
 * @since 2023-11-20
 */
@Service
public class MedicalRecordServiceImpl extends ServiceImpl<MedicalRecordMapper, MedicalRecord> implements IMedicalRecordService {

    @Autowired
    private MedicalRecordMapper mapper;
    @Autowired
    private MedicalDiseaseMapper diseaseMapper;

    @Override
    public MedicalRecordVo getMedicalRecordByRegistId(Integer registId) {
        //第一步：查询病历基本信息
        MedicalRecordVo medicalRecord = mapper.getMedicalRecordByRegistId(registId);
        //第二步：如果病历存在，则查询其关联的诊断信息
        if(medicalRecord!=null){
            List<MedicalDiseaseVo> diseases = mapper.getMedicalDiseasesByMedicalId(medicalRecord.getId());
            medicalRecord.setMedicalDiseases(diseases);
        }
        return medicalRecord;
    }

    @Transactional
    @Override
    public boolean saveMedicalRecord(MedicalRecordVo medicalRecordVo) {
        //保存病历信息
        MedicalRecord medicalRecord = new MedicalRecord();
        BeanUtils.copyProperties(medicalRecordVo,medicalRecord);
        super.saveOrUpdate(medicalRecord);
        //先删
        LambdaQueryWrapper<MedicalDisease> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(MedicalDisease::getMedicalID,medicalRecord.getId());
        diseaseMapper.delete(wrapper);
        //再加
        List<MedicalDiseaseVo> list = medicalRecordVo.getMedicalDiseases();
        if(list!=null && !list.isEmpty()) {
            for (MedicalDiseaseVo diseaseVo : list) {
                if (diseaseVo.getDisease() != null && diseaseVo.getDisease().getId() != null) {
                    MedicalDisease medicalDisease = new MedicalDisease();
                    medicalDisease.setMedicalID(medicalRecord.getId());
                    medicalDisease.setRegistID(medicalRecord.getRegistId());
                    medicalDisease.setDiseaseID(diseaseVo.getDisease().getId());
                    medicalDisease.setDiagnoseType(diseaseVo.getDiagnoseType());
                    medicalDisease.setDiagnoseCate(diseaseVo.getDiagnoseCate());
                    diseaseMapper.insert(medicalDisease);
                }
            }
        }

        return true;
    }
}
