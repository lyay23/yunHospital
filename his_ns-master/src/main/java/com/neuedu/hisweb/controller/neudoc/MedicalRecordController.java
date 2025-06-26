package com.neuedu.hisweb.controller.neudoc;



import com.neuedu.hisweb.entity.JsonResult;
import com.neuedu.hisweb.entity.MedicalRecord;
import com.neuedu.hisweb.entity.Register;
import com.neuedu.hisweb.entity.vo.MedicalRecordVo;
import com.neuedu.hisweb.service.IMedicalRecordService;
import com.neuedu.hisweb.service.IRegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author lynn
 * @since 2023-11-21
 */
@RestController
@RequestMapping("/neudoc/medicalrecord")
public class MedicalRecordController {

    @Autowired
    private IMedicalRecordService iMedicalRecordService;
    @Autowired
    private IRegisterService iRegisterService;
    /**
     * 医生根据挂号ID，得到患者的病历信息
     */
    @GetMapping("/getByRegistId")
    public JsonResult<MedicalRecordVo> getMedicalRecordByRegistId(@RequestParam("registId") Integer registId) {
        MedicalRecordVo medicalRecord = iMedicalRecordService.getMedicalRecordByRegistId(registId);
        return JsonResult.success(medicalRecord);
    }

    @PostMapping("/save")
    public JsonResult<MedicalRecordVo> saveMedicalRecord(@RequestBody MedicalRecordVo medicalRecord) {
        try {
            MedicalRecordVo result = iMedicalRecordService.saveMedicalRecord(medicalRecord);
            return JsonResult.success(result);
        } catch (Exception e) {
            return JsonResult.error("保存异常: " + e.getMessage());
        }
    }

    @PutMapping("/update")
    public JsonResult<Boolean> updateMedicalRecord(@RequestBody MedicalRecord medicalRecord) {
        try {
            boolean success = iMedicalRecordService.updateById(medicalRecord);
            return JsonResult.success(success);
        } catch (Exception e) {
            return JsonResult.error("更新异常: " + e.getMessage());
        }
    }

    @PostMapping("/finish")
    public JsonResult<Object> finishConsultation(@RequestBody Register register) {
        if (register == null || register.getId() == null) {
            return new JsonResult<>("参数错误");
        }
        // Assuming state '3' means 'finished'
        boolean success = iRegisterService.updateVisitState(register.getId(), 3);

        if (success) {
            return new JsonResult<>(true);
        } else {
            return new JsonResult<>("操作失败");
        }
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

