package com.neuedu.hisweb.controller.neusys;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.hisweb.entity.Settlecategory;
import com.neuedu.hisweb.entity.JsonResult;
import com.neuedu.hisweb.service.ISettlecategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 * 结算类别
 * @author lynn
 * @since 2023-07-31
 */
@RestController
@RequestMapping("/settlecategory")
public class SettlecategoryController {

    @Autowired
    private ISettlecategoryService iService;

    @GetMapping("/page")
    public JsonResult<Page> getConstantPage(@RequestParam(value = "pn", defaultValue = "1") Integer pn,
                                            @RequestParam(value = "count", defaultValue = "10") Integer count,
                                            @RequestParam(value = "keyword",required = false)String keyword){
        Page<Settlecategory> page=Page.of(pn,count);
        LambdaQueryWrapper<Settlecategory> queryWrapper=new LambdaQueryWrapper<>();
        if(keyword!=null){
            queryWrapper.like(Settlecategory::getSettleCode,keyword)
                    .or().like(Settlecategory::getSettleName,keyword);
        }
        queryWrapper.orderByAsc(Settlecategory::getSequenceNo);
        iService.page(page);

        return new JsonResult<Page>(page);
    }

    @PostMapping("/add")
    public JsonResult<Settlecategory> addSettlecategory(@RequestBody Settlecategory settlecategory){
        boolean rs= iService.save(settlecategory);
        if(rs)return new JsonResult<Settlecategory>(settlecategory);
        else return new JsonResult<>("添加失败");
    }

    @PostMapping("/update")
    public JsonResult<Settlecategory> updateSettlecategory(@RequestBody Settlecategory settlecategory){
        boolean rs= iService.updateById(settlecategory);
        if(rs)return new JsonResult<Settlecategory>(settlecategory);
        else return new JsonResult<>("修改失败");
    }

    @PostMapping("/del")
    public JsonResult<Settlecategory> delSettlecategory(@RequestParam(value = "id",required = true) Integer id){
        boolean rs= iService.removeById(id);
        if(rs){
            JsonResult<Settlecategory> jsonResult= new JsonResult<>();
            jsonResult.setResult(true);
            return jsonResult;
        }
        else return new JsonResult<>("删除失败");
    }

}

