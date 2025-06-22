package com.neuedu.hisweb.controller.neusys;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.hisweb.entity.JsonResult;
import com.neuedu.hisweb.entity.Scheduling;

import com.neuedu.hisweb.entity.vo.SchedulingVo;
import com.neuedu.hisweb.service.ISchedulingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Collection;
import java.util.Collections;
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
@RequestMapping("/scheduling")
public class SchedulingController {
    @Autowired
    private ISchedulingService iService;

    @GetMapping("/page")
    public JsonResult<Page> getSchedulingPage(@RequestParam(value = "pn", defaultValue = "1") Integer pn,
                                        @RequestParam(value = "count", defaultValue = "10") Integer count,
                                        @RequestParam(value = "keyword",required = false)String keyword,
                                        @RequestParam(value = "deptId",required = false)String deptId,
                                              @RequestParam(value = "userId",required = false)String userId,
                                              @RequestParam(value = "regLevel",required = false)String  regLevel,
                                              @RequestParam(value = "noon",required = false)String  noon,
                                              @RequestParam(value = "start",required = false)String start,
                                              @RequestParam(value = "end",required = false)String end){
        Page<SchedulingVo> page=Page.of(pn,count);
        iService.selectPage(page,keyword,deptId,userId,regLevel,noon,start,end);
        return new JsonResult<Page>(page);
    }

    @PostMapping("/add")
    public JsonResult<Scheduling> addScheduling(@RequestBody Collection<Scheduling> schedulings){
        try {
            boolean rs= iService.saveBatch(schedulings);
            if(rs){
                JsonResult<Scheduling> result = new JsonResult<>();
                result.setResult(true);
                return result;
            } else {
                return new JsonResult<>("新增失败");
            }
        }catch (Exception ex){
            // 考虑记录日志 ex.printStackTrace();
            return  new JsonResult<>("新增失败，可能存在重复的排班数据。");
        }
    }

    @PostMapping("/update")
    public JsonResult<Scheduling> updateScheduling(@RequestBody Scheduling scheduling){
        boolean rs= iService.updateById(scheduling);
        if(rs)return new JsonResult<Scheduling>(scheduling);
        else return new JsonResult<>("修改失败");
    }

    @PostMapping("/del")
    public JsonResult<Scheduling> delScheduling(@RequestParam(value = "id",required = true) Integer id){
        boolean rs= iService.removeById(id);
        if(rs){
            JsonResult<Scheduling> jsonResult= new JsonResult<>();
            jsonResult.setResult(true);
            return jsonResult;
        }
        else return new JsonResult<>("删除失败");
    }

}

