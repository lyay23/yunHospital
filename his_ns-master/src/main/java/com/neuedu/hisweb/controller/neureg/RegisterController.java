package com.neuedu.hisweb.controller.neureg;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.hisweb.entity.*;
import com.neuedu.hisweb.entity.vo.MedicalCardVo;
import com.neuedu.hisweb.entity.vo.RegParam;
import com.neuedu.hisweb.entity.vo.RegisterVo;
import com.neuedu.hisweb.entity.vo.SchedulingVo;
import com.neuedu.hisweb.service.IRegisterService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <p>
 *  前端控制器
 * </p>
 * 挂号
 * @author lynn
 * @since 2023-08-09
 */
@RestController
@RequestMapping("/register")
public class RegisterController {

    @Autowired
    private IRegisterService iService;

    @PostMapping("/add")
    public JsonResult<Register> addUser(@RequestBody RegParam param){
        boolean rs= iService.saveRegister(param);
        if(rs)return new JsonResult<Register>(param.getRegister());
        else return new JsonResult<>("添加失败");
    }


    @GetMapping("/page")
    public JsonResult<Page> getRegPage(@RequestParam(value = "pn", defaultValue = "1") Integer pn,
                                       @RequestParam(value = "count", defaultValue = "10") Integer count,
                                       @RequestParam(value = "deptId",required = false)Integer deptId,
                                       @RequestParam(value = "docId",required = false)Integer doctId,
                                       @RequestParam(value = "regDate",required = false)String regDate,
                                       @RequestParam(value = "state",required = false)Integer state,
                                       @RequestParam(value = "keyword",required = false)String keyword){
        Page<RegisterVo> page=Page.of(pn,count);
        iService.selectPage(page, deptId,doctId, state,keyword, regDate);
        return new JsonResult<Page>(page);
    }

    @PostMapping("/backOff")
    public JsonResult<Register>  backOff(@RequestBody RegParam param){
        boolean rs= iService.updateRegisterState(param);
        if(rs)return new JsonResult<Register>(rs);
        else return new JsonResult<>("退号失败");
    }

}

