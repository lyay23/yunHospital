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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
                                        @RequestParam(value = "expClassIds",required = false) List<String> expClassIds,
                                        @RequestParam(value = "dept",required = false)String dept){
        Page<FmeditemVo> page=Page.of(pn,count);
        iService.selectPage(page,keyword,expClassIds,dept);
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



    @DeleteMapping("/del")
    public JsonResult<Object> delFmeditem(@RequestParam(value = "id",required = true) Integer id){
        boolean rs= iService.removeById(id);
        if(rs){
            JsonResult<Object> jsonResult= new JsonResult<>();
            jsonResult.setResult(true);
            return jsonResult;
        }
        else return new JsonResult<>("删除失败");
    }

    @PostMapping("/del-batch")
    public JsonResult<Object> delBatchFmeditem(@RequestBody List<Integer> ids){
        boolean rs= iService.removeByIds(ids);
        if(rs){
            JsonResult<Object> jsonResult= new JsonResult<>();
            jsonResult.setResult(true);
            return jsonResult;
        }
        else return new JsonResult<>("删除失败");
    }

    @PostMapping("/import")
    public JsonResult<Integer> importFmeditem(MultipartFile file){
        Integer count=iService.importFmeditem(file);
        if(count>0)return new JsonResult<>(count);
        else return new JsonResult<>("导入失败");
    }
}

