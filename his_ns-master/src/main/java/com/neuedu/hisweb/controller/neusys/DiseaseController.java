package com.neuedu.hisweb.controller.neusys;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.hisweb.entity.Disease;
import com.neuedu.hisweb.entity.JsonResult;
import com.neuedu.hisweb.entity.Settlecategory;
import com.neuedu.hisweb.entity.vo.ConstantItemVo;
import com.neuedu.hisweb.entity.vo.DiseaseVo;
import com.neuedu.hisweb.service.IDiseaseService;
import com.neuedu.hisweb.service.ISettlecategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author lynn
 * @since 2023-07-31
 */
@RestController
@RequestMapping("/disease")
public class DiseaseController {
    @Autowired
    private IDiseaseService iService;

    @GetMapping("/page")
    public JsonResult<Page> getConstantPage(@RequestParam(value = "pn", defaultValue = "1") Integer pn,
                                            @RequestParam(value = "count", defaultValue = "10") Integer count,
                                            @RequestParam(value = "keyword",required = false)String keyword,
                                            @RequestParam(value = "ctype",required = false)String ctype){
        Page<DiseaseVo> page=Page.of(pn,count);
        iService.selectPage(page,keyword,ctype);
        return new JsonResult<Page>(page);
    }


    @PostMapping("/add")
    public JsonResult<Disease> addSettlecategory(@RequestBody Disease disease){
        boolean rs= iService.save(disease);
        if(rs)return new JsonResult<Disease>(disease);
        else return new JsonResult<>("添加失败");
    }

    @PostMapping("/update")
    public JsonResult<Disease> updateSettlecategory(@RequestBody Disease disease){
        boolean rs= iService.updateById(disease);
        if(rs)return new JsonResult<Disease>(disease);
        else return new JsonResult<>("修改失败");
    }

    @PostMapping("/del")
    public JsonResult<Disease> delSettlecategory(@RequestParam(value = "id",required = true) Integer id){
        boolean rs= iService.removeById(id);
        if(rs){
            JsonResult<Disease> jsonResult= new JsonResult<>();
            jsonResult.setResult(true);
            return jsonResult;
        }
        else return new JsonResult<>("删除失败");
    }
}

