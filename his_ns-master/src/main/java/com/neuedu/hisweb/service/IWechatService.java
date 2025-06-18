package com.neuedu.hisweb.service;

import com.neuedu.hisweb.entity.Customer;
import com.neuedu.hisweb.entity.JsonResult;

public interface IWechatService {
    JsonResult<Object> wechatLogin(String code);
    JsonResult<Object> wechatReg(Customer customer, String openId);
}

