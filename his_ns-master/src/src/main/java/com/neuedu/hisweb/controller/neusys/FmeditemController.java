package com.neuedu.hisweb.controller.neusys;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.hisweb.entity.Fmeditem;
import com.neuedu.hisweb.entity.JsonResult;
import com.neuedu.hisweb.entity.User;
import com.neuedu.hisweb.entity.vo.FmeditemVo;
import com.neuedu.hisweb.entity.vo.UserVo;
import com.neuedu.hisweb.service.IFmeditemService;
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
@RequestMapping("/fmeditem")
public class FmeditemController {

    @Autowired
    private IFmeditemService iService;

    @GetMapping("/page")
    public JsonResult<Page> getFmeditemPage(@RequestParam(value = "pn", defaultValue = "1") Integer pn,
                                        @RequestParam(value = "count", defaultValue = "10") Integer count,
                                        @RequestParam(value = "keyword",required = false)String keyword,
                                        @RequestParam(value = "expClassId",required = false)String expClassId,
                                        @RequestParam(value = "dept",required = false)String dept,
                                        @RequestParam(value = "ctype",required = false)String ctype){
        Page<FmeditemVo> page=Page.of(pn,count);
        iService.selectPage(page,keyword,expClassId,dept,ctype);
        return new JsonResult<Page>(page);
    }

    @PostMapping("/add")
    public JsonResult<Fmeditem>addFmeditem(@RequestBody Fmeditem fmeditem){
        boolean rs= iService.save(fmeditem);
        if(rs)return new JsonResult<Fmeditem>(fmeditem);
        else return new JsonResult<>("添加失败");
    }

    @PostMapping("/update")
    public JsonResult<Fmeditem> updateUser(@RequestBody Fmeditem fmeditem){
        boolean rs= iService.updateById(fmeditem);
        if(rs)return new JsonResult<Fmeditem>(fmeditem);
        else return new JsonResult<>("修改失败");
    }



    @PostMapping("/del")
    public JsonResult<Object> delUser(@RequestParam(value = "id",required = true) Integer id){
        boolean rs= iService.removeById(id);
        if(rs){
            JsonResult<Object> jsonResult= new JsonResult<>();
            jsonResult.setResult(true);
            return jsonResult;
        }
        else return new JsonResult<>("删除失败");
    }
}

