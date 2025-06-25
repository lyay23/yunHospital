package com.neuedu.hisweb.controller.neudoc;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.neuedu.hisweb.entity.Disease;
import com.neuedu.hisweb.entity.JsonResult;
import com.neuedu.hisweb.entity.MedicalDisease;
import com.neuedu.hisweb.service.IDiseaseService;
import com.neuedu.hisweb.service.IMedicalDiseaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author lynn
 * @since 2023-11-21
 */
@RestController
@RequestMapping("/neudoc/medicaldisease")
public class MedicalDiseaseController {

    @Autowired
    private IMedicalDiseaseService iMedicalDiseaseService;

    @Autowired
    private IDiseaseService iDiseaseService;

    @GetMapping("/list")
    public JsonResult<List<Map<String, Object>>> list(@RequestParam("medicalId") Integer medicalId,
                                                      @RequestParam(value = "diagnoseType", required = false) Integer diagnoseType) {
        LambdaQueryWrapper<MedicalDisease> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MedicalDisease::getMedicalID, medicalId);
        if (diagnoseType != null) {
            queryWrapper.eq(MedicalDisease::getDiagnoseType, diagnoseType);
        }
        List<MedicalDisease> list = iMedicalDiseaseService.list(queryWrapper);

        List<Map<String, Object>> voList = list.stream().map(md -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", md.getId());
            map.put("medicalId", md.getMedicalID());
            map.put("diseaseId", md.getDiseaseID());
            map.put("diagnoseType", md.getDiagnoseType());
            map.put("getTime", md.getSiskDate());
            map.put("diagnoseState", md.getDiagnoseCate());
            
            Disease disease = iDiseaseService.getById(md.getDiseaseID());
            map.put("disease", disease);
            return map;
        }).collect(Collectors.toList());

        return JsonResult.success(voList);
    }

    @PostMapping("/savebatch")
    public JsonResult<Boolean> saveBatch(@RequestBody List<MedicalDisease> medicalDiseases) {
        if (medicalDiseases == null || medicalDiseases.isEmpty()) {
            return JsonResult.success(true); 
        }

        Integer medicalId = medicalDiseases.get(0).getMedicalID();
        if (medicalId == null) {
            return JsonResult.error("MedicalId 不能为空");
        }
        LambdaQueryWrapper<MedicalDisease> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(MedicalDisease::getMedicalID, medicalId);
        deleteWrapper.eq(MedicalDisease::getDiagnoseType, 2);
        iMedicalDiseaseService.remove(deleteWrapper);

        for (MedicalDisease md : medicalDiseases) {
            md.setId(null);
        }
        
        boolean success = iMedicalDiseaseService.saveBatch(medicalDiseases);
        return JsonResult.success(success);
    }

    @DeleteMapping("/del/{id}")
    public JsonResult<Boolean> del(@PathVariable("id") Integer id) {
        boolean success = iMedicalDiseaseService.removeById(id);
        return JsonResult.success(success);
    }
}

