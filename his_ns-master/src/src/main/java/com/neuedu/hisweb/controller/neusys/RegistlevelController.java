package com.neuedu.hisweb.controller.neusys;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.hisweb.entity.ConstantType;
import com.neuedu.hisweb.entity.JsonResult;
import com.neuedu.hisweb.entity.Registlevel;
import com.neuedu.hisweb.service.IConstantTypeService;
import com.neuedu.hisweb.service.IRegistlevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author lynn
 * @since 2023-07-30
 */
@RestController
@RequestMapping("/registlevel")
public class RegistlevelController {

    @Autowired
    private IRegistlevelService iRegistlevelService;

    @GetMapping("/page")
    public JsonResult<Page> getRegistlevelPage(@RequestParam(value = "pn", defaultValue = "1") Integer pn,
                                            @RequestParam(value = "count", defaultValue = "10") Integer count,
                                            @RequestParam(value = "keyword",required = false)String keyword){
        Page<Registlevel> page=Page.of(pn,count);
        iRegistlevelService.selectPage(page,keyword);
        return new JsonResult<Page>(page);
    }


    @PostMapping("/add")
    public JsonResult<Registlevel> addConstantType(@RequestBody Registlevel registlevel){
        boolean rs= iRegistlevelService.save(registlevel);
        if(rs)return new JsonResult<Registlevel>(registlevel);
        else return new JsonResult<>("添加失败");
    }

    @PostMapping("/update")
    public JsonResult<Registlevel> updateConstantType(@RequestBody Registlevel registlevel){
        boolean rs= iRegistlevelService.updateById(registlevel);
        if(rs)return new JsonResult<Registlevel>(registlevel);
        else return new JsonResult<>("修改失败");
    }

    @PostMapping("/del")
    public JsonResult<Registlevel> delConstantType(@RequestParam(value = "id",required = true) Integer id){
        boolean rs= iRegistlevelService.removeById(id);
        if(rs){
            JsonResult<Registlevel> jsonResult= new JsonResult<>();
            jsonResult.setResult(true);
            return jsonResult;
        }
        else return new JsonResult<>("删除失败");
    }
}

