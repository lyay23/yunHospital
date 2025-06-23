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
 * @author lynn
 * @since 2023-11-21
 */
@Service
public class MedicalRecordServiceImpl extends ServiceImpl<MedicalRecordMapper, MedicalRecord> implements IMedicalRecordService {

    @Autowired
    private MedicalDiseaseMapper medicalDiseaseMapper;
    @Autowired
    private RegisterMapper registerMapper;
    @Autowired
    private MedicalRecordMapper medicalRecordMapper;

    @Override
    public MedicalRecordVo getMedicalRecordByRegistId(Integer registId) {
        return getBaseMapper().getMedicalRecordByRegistId(registId);
    }

    @Override
    @Transactional
    public boolean saveMedicalRecord(MedicalRecordVo vo) {
        MedicalRecord medicalRecord = new MedicalRecord();
        BeanUtils.copyProperties(vo, medicalRecord);

        // 如果没有病历ID，说明是新建
        if(medicalRecord.getId() == null){
            // 通过挂号ID查询是否已经存在病历，防止重复创建
            LambdaQueryWrapper<MedicalRecord> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(MedicalRecord::getRegistId, vo.getRegistId());
            MedicalRecord existingRecord = getBaseMapper().selectOne(queryWrapper);
            if (existingRecord != null) {
                medicalRecord.setId(existingRecord.getId());
                baseMapper.updateById(medicalRecord);
            } else {
                if(medicalRecord.getRegistId() != null) {
                    // 使用挂号ID作为病历号
                    medicalRecord.setCaseNumber(medicalRecord.getRegistId().toString());
                }
                baseMapper.insert(medicalRecord);
            }
        } else {
            baseMapper.updateById(medicalRecord);
        }

        // 先删除旧的诊断记录
        medicalDiseaseMapper.delete(new QueryWrapper<MedicalDisease>().eq("MedicalID", medicalRecord.getId()));

        // 再插入新的诊断记录
        if (vo.getMedicalDiseases() != null && !vo.getMedicalDiseases().isEmpty()) {
            for (MedicalDiseaseVo diseaseVo : vo.getMedicalDiseases()) {
                if(diseaseVo.getDiseaseID() == null) continue; //跳过没有选择疾病的空行
                MedicalDisease medicalDisease = new MedicalDisease();
                BeanUtils.copyProperties(diseaseVo, medicalDisease);
                medicalDisease.setId(null);
                medicalDisease.setMedicalId(medicalRecord.getId());
                medicalDisease.setRegistId(vo.getRegistId());
                medicalDiseaseMapper.insert(medicalDisease);
            }
        }

        return true;
    }
}
