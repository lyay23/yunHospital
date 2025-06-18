package com.neuedu.hisweb.controller.neusys;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.hisweb.entity.ConstantType;
import com.neuedu.hisweb.entity.JsonResult;
import com.neuedu.hisweb.service.IConstantTypeService;
import com.neuedu.hisweb.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author lynn
 * @since 2023-07-25
 */
@RestController
@RequestMapping("/constant")
public class ConstantTypeController {

    @Autowired
    private IConstantTypeService iConstantTypeService;

    @GetMapping("/page")
    public JsonResult<Page> getConstantPage(@RequestParam(value = "pn", defaultValue = "1") Integer pn,
                                            @RequestParam(value = "count", defaultValue = "10") Integer count,
                                            @RequestParam(value = "keyword",required = false)String keyword){
        Page<ConstantType> page=Page.of(pn,count);
        if(keyword!=null){
            LambdaQueryWrapper<ConstantType> queryWrapper=new LambdaQueryWrapper<>();
            queryWrapper.like(ConstantType::getConstantTypeName,keyword)
                    .or().like(ConstantType::getConstantTypeCode,keyword);
            iConstantTypeService.page(page,queryWrapper);
        }else
            iConstantTypeService.page(page);

        return new JsonResult<Page>(page);
    }


    @PostMapping("/add")
    public JsonResult<ConstantType> addConstantType(@RequestBody ConstantType constantType){
       boolean rs= iConstantTypeService.save(constantType);
       if(rs)return new JsonResult<ConstantType>(constantType);
       else return new JsonResult<>("添加失败");
    }

    @PostMapping("/update")
    public JsonResult<ConstantType> updateConstantType(@RequestBody ConstantType constantType){
        boolean rs= iConstantTypeService.updateById(constantType);
        if(rs)return new JsonResult<ConstantType>(constantType);
        else return new JsonResult<>("修改失败");
    }

    @PostMapping("/del")
    public JsonResult<ConstantType> delConstantType(@RequestParam(value = "id",required = true) Integer id){
        boolean rs= iConstantTypeService.removeById(id);
        if(rs){
            JsonResult<ConstantType> jsonResult= new JsonResult<>();
            jsonResult.setResult(true);
            return jsonResult;
        }
        else return new JsonResult<>("删除失败");
    }

}

