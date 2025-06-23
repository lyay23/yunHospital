package com.neuedu.hisweb.controller.neusys;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.hisweb.entity.ConstantType;
import com.neuedu.hisweb.entity.Disease;
import com.neuedu.hisweb.entity.Disecategory;
import com.neuedu.hisweb.entity.JsonResult;
import com.neuedu.hisweb.entity.vo.DiseaseVo;
import com.neuedu.hisweb.entity.vo.DisecategoryVo;
import com.neuedu.hisweb.service.IDiseaseService;
import com.neuedu.hisweb.service.IDisecategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@RequestMapping("/disecategory")
public class DisecategoryController {

    private static final Logger logger = LoggerFactory.getLogger(DisecategoryController.class);

    @Autowired
    private IDisecategoryService iService;

    @GetMapping("/page")
    public JsonResult<Page<DisecategoryVo>> getConstantPage(@RequestParam(value = "pn", defaultValue = "1") Integer pn,
                                            @RequestParam(value = "count", defaultValue = "10") Integer count,
                                            @RequestParam(value = "keyword",required = false)String keyword,
                                                      @RequestParam(value = "dicaName",required = false)String dicaName,
                                                      @RequestParam(value = "dicaType",required = false)Integer dicaType){

        logger.info("请求参数: pn={}, count={}, keyword={}, dicaName={}, dicaType={}", pn, count, keyword, dicaName, dicaType);
        Page<DisecategoryVo> page = iService.selectPage(pn,count,keyword,dicaName,dicaType);
        logger.info("返回数据: {}", page.getRecords());
        return JsonResult.success(page);
    }


    @PostMapping("/add")
    public JsonResult<Disecategory> addSettlecategory(@RequestBody Disecategory disecategory){
        boolean rs= iService.save(disecategory);
        if(rs)return new JsonResult<Disecategory>(disecategory);
        else return new JsonResult<>("添加失败");
    }

    @PostMapping("/update")
    public JsonResult<Disecategory> updateSettlecategory(@RequestBody Disecategory disecategory){
        boolean rs= iService.updateById(disecategory);
        if(rs)return new JsonResult<Disecategory>(disecategory);
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

