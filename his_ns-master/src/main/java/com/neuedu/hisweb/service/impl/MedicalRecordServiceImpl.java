package com.neuedu.hisweb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private MedicalDiseaseMapper medicalDiseaseMapper;
    @Autowired
    private RegisterMapper registerMapper;
    @Autowired
    private MedicalRecordMapper medicalRecordMapper;

    @Override
    public MedicalRecord getByRid(Integer rid) {
        LambdaQueryWrapper<MedicalRecord> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(MedicalRecord::getRegistID,rid);
        return medicalRecordMapper.selectOne(queryWrapper);
    }


//    @Override
//    public MedicalRecordVo getOne(Integer rid, Integer type) {
//        MedicalRecordVo medicalRecord= getBaseMapper().getOne(rid);
//        if (medicalRecord!=null){
//          List<MedicalDiseaseVo> medicalDiseases=medicalDiseaseMapper.getListByRid(rid,type);
//          medicalRecord.setMedicalDiseases(medicalDiseases);
//        }
//        return medicalRecord;
//    }



//    public Boolean saveMedicalRecord(MedicalRecordVo medicalRecord) {
//        int num=0;
//        if(medicalRecord!=null) {
//            //更新病历信息
//            num += this.saveOrUpdate(medicalRecord);
//            //更新诊断信息
//            if(medicalRecord.getMedicalDiseases()!=null
//                    &&medicalRecord.getMedicalDiseases().size()>0) {
//                //删除历史诊断信息
//                LambdaQueryWrapper<MedicalDiseaseVo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//                lambdaQueryWrapper.eq(MedicalDiseaseVo::getRegistID, medicalRecord.getRegistID())
//                        .eq(MedicalDiseaseVo::getDiagnoseCate, 1);
////                num += medicalDiseaseMapper.delete(lambdaQueryWrapper);
////
////                //批量添加新的诊断信息
////                num += medicalDiseaseMapper.insertBatchSomeColumn(medicalRecord.getMedicalDiseases());
//            }
//        }
//        //如果是"提交"，改变患者状态为"已经诊断"
//        if("2".equals(medicalRecord.getCaseState())){
//            num += registerMapper.updateVisitState(medicalRecord.getRegistID(),2);
//        }
//        return num>0?true:false;
//    }

//    public int saveMedicalRecord(MedicalRecord medicalRecord){
//        LambdaQueryWrapper<MedicalRecord> wrapper = new LambdaQueryWrapper<>();
//        wrapper.eq(MedicalRecord::getRegistID, medicalRecord.getRegistID());
//        MedicalRecordVo mr = this.getBaseMapper().selectOne(wrapper);
//        int rs=0;
//
//        if(mr!=null) {
//            medicalRecord.setId(mr.getId());
//            rs= this.getBaseMapper().updateById(medicalRecord);
//        }else {
//            rs = this.getBaseMapper().insert(medicalRecord);
//        }
//        return rs;
//    }

    @Override
    public Boolean saveMedicalRecord(MedicalRecord medicalRecord) {
        LambdaQueryWrapper<MedicalRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MedicalRecord::getRegistID, medicalRecord.getRegistID());
        MedicalRecord existingRecord = medicalRecordMapper.selectOne(queryWrapper);

        if(existingRecord != null){
            // An existing record is found. Update it.
            medicalRecord.setId(existingRecord.getId()); 
            return medicalRecordMapper.updateById(medicalRecord) > 0;
        }else{
            // No existing record. Insert a new one.
            Register register = registerMapper.selectById(medicalRecord.getRegistID());
            if (register != null) {
                medicalRecord.setCaseNumber(register.getCaseNumber());
            } else {
                return false;
            }
            if(medicalRecord.getCaseState() == null){
                 medicalRecord.setCaseState(1);
            }
            return medicalRecordMapper.insert(medicalRecord) > 0;
        }
    }

}
