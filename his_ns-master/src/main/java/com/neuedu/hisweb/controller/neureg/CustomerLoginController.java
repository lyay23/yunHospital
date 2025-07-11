package com.neuedu.hisweb.controller.neureg;

import com.neuedu.hisweb.entity.Customer;
import com.neuedu.hisweb.entity.JsonResult;
import com.neuedu.hisweb.service.ICustomerService;
import com.neuedu.hisweb.utils.JwtUtils;
import com.neuedu.hisweb.utils.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/yunapp/customer")
public class CustomerLoginController {

    @Autowired
    private ICustomerService customerService;

    @Autowired
    private JwtUtils jwtUtils;

    // 使用一个静态内部类来接收登录请求的JSON数据
    public static class LoginRequest {
        private String idnumber;
        private String password;

        // Getters and Setters
        public String getIdnumber() {
            return idnumber;
        }

        public void setIdnumber(String idnumber) {
            this.idnumber = idnumber;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    @PostMapping("/login")
    public JsonResult<Map<String, Object>> login(@RequestBody LoginRequest loginRequest) {
        String idnumber = loginRequest.getIdnumber();
        String password = loginRequest.getPassword();

        if (idnumber == null || password == null) {
            return new JsonResult<>("身份证号或密码不能为空");
        }

        Customer customer = customerService.selectByIdNumber(idnumber);

        if (customer == null) {
            return new JsonResult<>("用户不存在");
        }

        // 使用MD5加密后比较密码,忽略大小写
        if (customer.getPassword().equalsIgnoreCase(MD5Util.getMD5(password))) {
            // 密码匹配，生成JWT
            String token = jwtUtils.createToken(customer);

            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("customer", customer);

            // 返回包含token和客户信息的数据
            return new JsonResult<>(data);
        } else {
            return new JsonResult<>("密码错误");
        }
    }
} 