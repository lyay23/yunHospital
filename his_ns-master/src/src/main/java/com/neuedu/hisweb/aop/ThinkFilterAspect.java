package com.neuedu.hisweb.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Aspect
@Component
public class ThinkFilterAspect {

    @Around("execution(public * com.neuedu.hisweb.controller..*(..))")
    public Object filterThink(ProceedingJoinPoint pjp) throws Throwable {
        Object result = pjp.proceed();

        if (result instanceof String str) {
            return removeThinkTags(str);
        } else if (result instanceof Flux<?> flux) {
            // 只处理Flux<String>
            return flux.map(item -> {
                if (item instanceof String s) {
                    return removeThinkTags(s);
                }
                return item;
            });
        }
        return result;
    }

    private String removeThinkTags(String content) {
        // 过滤 <think>...</think>，支持多行
        return content.replaceAll("(?s)<think>.*?</think>", "");
    }
}