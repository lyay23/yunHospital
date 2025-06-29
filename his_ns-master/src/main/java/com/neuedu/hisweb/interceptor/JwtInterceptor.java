package com.neuedu.hisweb.interceptor;

import com.neuedu.hisweb.entity.Customer;
import com.neuedu.hisweb.entity.JsonResult;
import com.neuedu.hisweb.entity.User;
import com.neuedu.hisweb.utils.JwtUtils;
import com.neuedu.hisweb.utils.UserUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

// 添加@Component注解，让Spring管理这个拦截器
@Component
public class JwtInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(JwtInterceptor.class);

    // 注入JwtUtils实例
    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("token");

        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        // 通过注入的实例调用verify方法
        if (null == token || "".equals(token) || !jwtUtils.verify(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 设置401状态码
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=utf-8");
            try (PrintWriter writer = response.getWriter()) {
                writer.print(new JsonResult<User>("未登录"));
            } catch (Exception e) {
                logger.error("login token error is {}", e.getMessage());
            }
            return false;
        }
        // 通过注入的实例调用getUserByToken方法
        Object userObj = jwtUtils.getUserByToken(token);
        if (userObj == null) {
            // token无效或已过期
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 设置401状态码
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=utf-8");
            try (PrintWriter writer = response.getWriter()) {
                writer.print(new JsonResult<User>("未登录"));
            } catch (Exception e) {
                logger.error("login token error is {}", e.getMessage());
            }
            return false;
        }
        if (userObj instanceof Customer){
            UserUtils.setLoginCustomer((Customer) userObj);
        }
        else{
            UserUtils.setLoginUser((User) userObj);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        System.out.println("执行了拦截器的postHandle方法");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserUtils.removeUser();
        UserUtils.removeCustomer();
    }
}