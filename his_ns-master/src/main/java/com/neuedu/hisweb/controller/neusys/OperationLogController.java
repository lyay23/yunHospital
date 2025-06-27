package com.neuedu.hisweb.controller.neusys;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.hisweb.aop.NoOperationLog;
import com.neuedu.hisweb.entity.OperationLog;
import com.neuedu.hisweb.entity.vo.OperationLogVo;
import com.neuedu.hisweb.service.impl.OperationLogServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/operationLog")
public class OperationLogController {
    @Autowired
    private OperationLogServiceImpl operationLogService;

    // 1. 普通分页查询
    @NoOperationLog
    @GetMapping("/page")
    public IPage<OperationLogVo> page(@RequestParam(defaultValue = "1") int current,
                                    @RequestParam(defaultValue = "10") int size,
                                    @RequestParam(required = false) String keyword) {
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(OperationLog::getUserName, keyword).or().like(OperationLog::getClassName, keyword);
        }
        wrapper.orderByDesc(OperationLog::getOperationTime);
        return operationLogService.selectPageVo(new Page<>(current, size), wrapper);
    }

    // 2. 按用户名查全部
    @NoOperationLog
    @GetMapping("/user/{userName}")
    public List<OperationLog> byUser(@PathVariable String userName) {
        return operationLogService.lambdaQuery().like(OperationLog::getUserName, userName).list();
    }

    // 3. 按方法名查全部
    @NoOperationLog
    @GetMapping("/method/{methodName}")
    public List<OperationLog> byMethod(@PathVariable String methodName) {
        return operationLogService.lambdaQuery().like(OperationLog::getMethodName, methodName).list();
    }

    // 4. 按类名查全部
    @NoOperationLog
    @GetMapping("/class/{className}")
    public List<OperationLog> byClass(@PathVariable String className) {
        return operationLogService.lambdaQuery().like(OperationLog::getClassName, className).list();
    }
} 