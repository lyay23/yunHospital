package com.neuedu.hisweb.controller.neureg;


import com.neuedu.hisweb.entity.JsonResult;
import com.neuedu.hisweb.entity.vo.PatientCostVo;
import com.neuedu.hisweb.service.IPatientcostsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
}

