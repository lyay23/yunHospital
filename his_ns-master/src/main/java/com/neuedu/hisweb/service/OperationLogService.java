package com.neuedu.hisweb.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.hisweb.entity.OperationLog;
import com.baomidou.mybatisplus.extension.service.IService;

public interface OperationLogService extends IService<OperationLog> {
    // 分页条件查询操作日志
    IPage<OperationLog> selectPage(Page<OperationLog> page, String userName, String methodName, String className);
} 