package com.neuedu.hisweb.utils;

import com.alibaba.fastjson2.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.text.MessageFormat;

@Component
public class WechatUtils {
    private static final String wxAppID = "your-app-id";
    private static final String wxAppSecret = "your-app-secret";

    @Autowired
    private RestTemplate restTemplate;

    public String requestWxLogin(String code){
        String reqUrl = MessageFormat.format("https://api.weixin.qq.com/sns/jscode2session" +
                "?appid={0}&secret={1}&js_code={2}&grant_type=authorization_code", wxAppID, wxAppSecret,code);
        String jsonRes = restTemplate.getForObject(reqUrl, String.class);
        System.out.println("微信登录返回：" + jsonRes);
        JSONObject jsonObject = JSONObject.parseObject(jsonRes);
        assert jsonObject != null;
        return jsonObject.get("openid").toString();
    }
}
