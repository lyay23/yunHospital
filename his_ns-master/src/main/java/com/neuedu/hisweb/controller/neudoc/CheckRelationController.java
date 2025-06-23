package com.neuedu.hisweb.controller.neudoc;

import com.neuedu.hisweb.entity.JsonResult;
import com.neuedu.hisweb.entity.vo.FmeditemVo;
import com.neuedu.hisweb.service.ICheckRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/checkrelation")
public class CheckRelationController {

    @Autowired
    private ICheckRelationService iService;

    @GetMapping("/items")
    public JsonResult<List<FmeditemVo>> getItemsByTemplateId(@RequestParam("templateId") Integer templateId) {
        List<FmeditemVo> items = iService.findItemsByTemplateId(templateId);
        return new JsonResult<>(items);
    }
} 