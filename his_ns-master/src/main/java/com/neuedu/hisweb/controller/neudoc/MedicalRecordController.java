package com.neuedu.hisweb.controller.neudoc;



import com.neuedu.hisweb.entity.JsonResult;
import com.neuedu.hisweb.entity.MedicalRecord;
import com.neuedu.hisweb.service.IMedicalRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author lynn
 * @since 2023-11-21
 */
@RestController
@RequestMapping("/neudoc")
public class MedicalRecordController {

    @Autowired
    private IMedicalRecordService iMedicalRecordService;
    /**
     * 医生根据病历号CaseNumber，得到患者的病历信息
     */
    @RequestMapping(value = "/getMedicalRecord", method = RequestMethod.GET)
    public JsonResult<MedicalRecord> getMedicalRecord(Integer rid) {
        MedicalRecord medicalRecord = iMedicalRecordService.getByRid(rid);
        return new JsonResult<MedicalRecord>(medicalRecord);
    }

    /**
     * 病历状态：1-暂存 2-已提交 3-诊毕
     * 医生根据患者id，暂存 患者病历信息  -- 如果不存在：新建；如果存在：修改
     * 医生根据患者id，保存 患者病历信息  -- 如果不存在：新建；如果存在：修改
     * 医生根据病历id和患者id，保存 患者确诊病历信息 -- 如果不存在：报错；如果存在：修改
     * 医生根据病历id和患者id，保存 患者病历信息状态为诊毕 -- 如果不存在：报错；如果存在：修改
     */
//    @RequestMapping(value = "/saveCheck", method = RequestMethod.GET)
//    public  JsonResult<MedicalRecord> saveCheck(MedicalRecord medicalRecord){
////       Boolean rs= iMedicalRecordService.saveMedicalRecord(medicalRecord);
////       return new JsonResult<MedicalRecord>(rs);
//    }


}

