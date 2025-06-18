package com.neuedu.hisweb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.neuedu.hisweb.entity.Customer;
import com.neuedu.hisweb.entity.CustomerBind;
import com.neuedu.hisweb.entity.JsonResult;
import com.neuedu.hisweb.mapper.CustomerBindMapper;
import com.neuedu.hisweb.mapper.CustomerMapper;
import com.neuedu.hisweb.service.IWechatService;
import com.neuedu.hisweb.utils.JwtUtils;
import com.neuedu.hisweb.utils.WechatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 微信服务实现类，处理微信相关业务逻辑
 * 提供微信登录、注册绑定等功能
 */
@Service
public class WechatServiceImpl implements IWechatService {

    @Autowired
    private WechatUtils wechatUtils;

    @Autowired
    private CustomerBindMapper customerBindMapper;

    @Autowired
    private CustomerMapper customerMapper;

    // 注入JWT工具类实例，用于生成JWT令牌
    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 微信登录方法
     * 通过微信授权码获取用户信息并登录
     *
     * @param code 微信授权码
     * @return 登录结果，包含用户信息和JWT令牌
     */
    @Override
    public JsonResult<Object> wechatLogin(String code) {
        Map<String, Object> resMap = new HashMap<>();

        // 调用微信工具类获取用户OpenID
        String openId = wechatUtils.requestWxLogin(code);

        // 查询是否已绑定用户
        List<CustomerBind> dataList = customerBindMapper.selectList(
                new LambdaQueryWrapper<CustomerBind>()
                        .eq(CustomerBind::getOpenId, openId));

        if (dataList.isEmpty()) {
            // 未绑定用户，返回OpenID给前端，引导用户注册
            resMap.put("openId", openId);
            return new JsonResult<>(resMap);
        } else {
            // 已绑定用户，获取用户信息
            Customer customer = customerMapper.selectById(dataList.get(0).getCustomerId());

            // 构造返回结果
            resMap.put("userInfo", customer);

            // 生成JWT令牌（通过注入的jwtUtils实例调用sign方法）
            resMap.put("token", jwtUtils.createToken(customer));

            return new JsonResult<>(resMap);
        }
    }

    /**
     * 微信用户注册方法
     * 将微信用户与系统用户进行绑定
     *
     * @param customer 用户信息
     * @param openId 微信OpenID
     * @return 注册结果
     */
    @Override
    public JsonResult<Object> wechatReg(Customer customer, String openId) {
        // 设置用户渠道为微信（182）
        customer.setChannel(182);

        // 设置用户状态为有效
        customer.setDelMark(1);

        // 设置创建时间
        customer.setCreatedate(new Date());

        // 保存用户信息
        customerMapper.insert(customer);

        // 创建用户与微信的绑定关系
        CustomerBind customerBind = new CustomerBind();
        customerBind.setCustomerId(customer.getId());
        customerBind.setCreateDate(new Date());
        customerBind.setOpenId(openId);

        // 保存绑定关系
        customerBindMapper.insert(customerBind);

        return new JsonResult<>(true);
    }
}