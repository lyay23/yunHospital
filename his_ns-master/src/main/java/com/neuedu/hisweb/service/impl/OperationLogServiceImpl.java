package com.neuedu.hisweb.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neuedu.hisweb.entity.OperationLog;
import com.neuedu.hisweb.entity.vo.OperationLogVo;
import com.neuedu.hisweb.mapper.OperationLogMapper;
import com.neuedu.hisweb.service.OperationLogService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lynn
 * @since 2024-03-18
 */
@Service
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements OperationLogService {

    public IPage<OperationLogVo> selectPageVo(IPage<OperationLogVo> page, Wrapper<OperationLog> wrapper) {
        return baseMapper.selectPageVo(page, wrapper);
    }

    @Override
    public IPage<OperationLog> selectPage(Page<OperationLog> page, String userName, String methodName, String className) {
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        if (userName != null && !userName.isEmpty()) {
            wrapper.like(OperationLog::getUserName, userName);
        }
        if (methodName != null && !methodName.isEmpty()) {
            wrapper.like(OperationLog::getMethodName, methodName);
        }
        if (className != null && !className.isEmpty()) {
            wrapper.like(OperationLog::getClassName, className);
        }
        wrapper.orderByDesc(OperationLog::getOperationTime);
        return this.page(page, wrapper);
    }
} 