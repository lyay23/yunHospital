package com.neuedu.hisweb.aop;

import com.neuedu.hisweb.entity.OperationLog;
import com.neuedu.hisweb.entity.User;
import com.neuedu.hisweb.service.OperationLogService;
import com.neuedu.hisweb.utils.JwtUtils;
import com.neuedu.hisweb.utils.UserUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Aspect
@Component
public class OperationLogAspect {
    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    private JwtUtils jwtUtils;

    @Around("execution(public * com.neuedu.hisweb.controller..*.*(..))")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // 排除带有@NoOperationLog注解的方法
        if (method.isAnnotationPresent(NoOperationLog.class) ||
            method.getDeclaringClass().isAnnotationPresent(NoOperationLog.class)) {
            return joinPoint.proceed();
        }

        // 采集操作方法和类名
        String methodName = method.getName();
        String lowerCaseMethodName = methodName.toLowerCase();
        if (lowerCaseMethodName.startsWith("get") || lowerCaseMethodName.startsWith("page") || lowerCaseMethodName.equals("login") || lowerCaseMethodName.equals("logout")) {
            return joinPoint.proceed();
        }

        Object result = joinPoint.proceed();

        // 优先从JWT token获取用户，因为ThreadLocal可能已被拦截器清除
        User user = null;
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String token = request.getHeader("token");
            if (token != null && !token.isEmpty()) {
                Object userObj = jwtUtils.getUserByToken(token);
                if (userObj instanceof User) {
                    user = (User) userObj;
                }
            }
        } catch (Exception e) {
            // 忽略异常，例如在非HTTP请求上下文中
        }

        // 如果token中没有，则尝试从ThreadLocal获取（主要用于兼容登录操作本身）
        if (user == null) {
            user = UserUtils.getLoginUser();
        }

        if (user == null) {
            System.out.println("[OperationLogAspect] 最终未能获取到登录用户，跳过日志记录. Method: " + method.getName());
            return result;
        }

        String className = method.getDeclaringClass().getName();

        // 构造日志对象
        OperationLog log = new OperationLog();
        log.setUserId(user.getId() != null ? user.getId().longValue() : null);
        log.setUserName(user.getUserName());
        log.setOperationTime(LocalDateTime.now());
        log.setMethodName(methodName);
        log.setClassName(className);

        operationLogService.save(log);
        return result;
    }
} 