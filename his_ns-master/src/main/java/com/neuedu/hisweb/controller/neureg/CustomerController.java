package com.neuedu.hisweb.controller.neureg;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.hisweb.entity.Customer;
import com.neuedu.hisweb.entity.JsonResult;
import com.neuedu.hisweb.entity.MedicalCard;
import com.neuedu.hisweb.entity.vo.CustomerVo;
import com.neuedu.hisweb.entity.vo.MedicalCardVo;
import com.neuedu.hisweb.service.ICustomerService;
import com.neuedu.hisweb.service.IMedicalCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

/**
 * <p>
 *  前端控制器
 * </p>
 * 注册用户
 * @author lynn
 * @since 2023-08-09
 */
@RestController
@RequestMapping("/customer")
public class CustomerController {
    @Autowired
    private ICustomerService iService;


    @GetMapping("/page")
    public JsonResult<Page> getUserPage(@RequestParam(value = "pn", defaultValue = "1") Integer pn,
                                        @RequestParam(value = "count", defaultValue = "10") Integer count,
                                        @RequestParam(value = "keyword",required = false)String keyword){
        Page<CustomerVo> page=Page.of(pn,count);
        iService.selectPage(page,keyword);
        return new JsonResult<Page>(page);
    }

    @GetMapping("/info/{id}")
    public JsonResult<Customer> info(@PathVariable("id") String id){
        return new JsonResult<Customer>(iService.getById(id));
    }

    @PostMapping("/add")
    public JsonResult<Customer>addUser(@RequestBody Customer customer){
        boolean rs= iService.save(customer);
        if(rs)return new JsonResult<Customer>(customer);
        else return new JsonResult<>("添加失败");
    }

    @PostMapping("/update")
    public JsonResult<Customer> updateUser(@RequestBody Customer customer){
        boolean rs= iService.updateById(customer);
        if(rs)return new JsonResult<Customer>(customer);
        else return new JsonResult<>("修改失败");
    }


    @PostMapping("/del")
    public JsonResult<Customer> delUser(@RequestParam(value = "id",required = true) Integer id){
        boolean rs= iService.removeById(id);
        if(rs){
            JsonResult<Customer> jsonResult= new JsonResult<>();
            jsonResult.setResult(true);
            return jsonResult;
        }
        else return new JsonResult<>("删除失败");
    }

}

