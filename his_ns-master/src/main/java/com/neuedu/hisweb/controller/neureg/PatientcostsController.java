package com.neuedu.hisweb.controller.neureg;


import com.neuedu.hisweb.entity.JsonResult;
import com.neuedu.hisweb.entity.vo.PatientCostVo;
import com.neuedu.hisweb.service.IPatientcostsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author lynn
 * @since 2023-08-09
 */
@RestController
@RequestMapping("/patientcosts")
public class PatientcostsController {
    @Autowired
    private IPatientcostsService iPatientcostsService;

    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public JsonResult<List<PatientCostVo>> getPatientCost(@RequestParam(value = "keyword",required = false) String keyword,
                                                          @RequestParam(value = "itemType",required = false) Integer itemType){
        List<PatientCostVo> list = iPatientcostsService.selectPatientCost(keyword,itemType);

        return new JsonResult<List<PatientCostVo>>(list);
    }

    @PostMapping("/doPay")
    public JsonResult<Object> doPay(@RequestBody Map<String,Object> params){
        Integer registId = (Integer) params.get("registId");
        boolean result = iPatientcostsService.doPay(java.util.Collections.singletonList(registId));
        if(result){
            return new JsonResult<>(true);
        }else{
            return new JsonResult<>("缴费失败");
        }
    }
}

