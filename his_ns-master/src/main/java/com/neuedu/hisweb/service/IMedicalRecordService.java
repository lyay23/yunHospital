package com.neuedu.hisweb.service;

import com.neuedu.hisweb.entity.MedicalRecord;
import com.baomidou.mybatisplus.extension.service.IService;
import com.neuedu.hisweb.entity.vo.MedicalRecordVo;

import java.io.Serializable;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lynn
 * @since 2023-08-09
 */
public interface IMedicalRecordService extends IService<MedicalRecord> {
    //MedicalRecordVo getOne(Integer rid, Integer type);
//    Boolean saveMedicalRecord(MedicalRecord medicalRecord);



     MedicalRecordVo getMedicalRecordByRegistId(Integer registId);

    boolean saveMedicalRecord(MedicalRecordVo medicalRecord);
}
