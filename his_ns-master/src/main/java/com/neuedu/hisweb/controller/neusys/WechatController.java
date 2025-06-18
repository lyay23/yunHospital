package com.neuedu.hisweb.controller.neusys;

import com.neuedu.hisweb.entity.Customer;
import com.neuedu.hisweb.entity.JsonResult;
import com.neuedu.hisweb.service.IWechatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  微信小程序相关逻辑控制器
 * </p>
 *
 * @author mazhihao
 * @since 2023-11-20
 */

@RestController
@RequestMapping("/wechat")
public class WechatController {
    @Autowired
    IWechatService iWechatService;

    @GetMapping(value = "/loginByCode")
    public JsonResult<Object> loginByCode(String code){
        return iWechatService.wechatLogin(code);
    }

    @PostMapping(value = "/reg/{id}")
    public JsonResult<Object> wechatReg(@RequestBody Customer customer, @PathVariable("id") String id) {
        return iWechatService.wechatReg(customer, id);
    }
}
