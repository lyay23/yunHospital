package com.neuedu.hisweb.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.neuedu.hisweb.entity.OperationLog;
import com.neuedu.hisweb.entity.vo.OperationLogVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLog> {
    // 使用BaseMapper的insert方法，不需要自定义SQL

    @Select("SELECT ol.*, u.RealName FROM operation_log ol LEFT JOIN user u ON ol.userName = u.UserName ${ew.customSqlSegment}")
    IPage<OperationLogVo> selectPageVo(IPage<OperationLogVo> page, @Param(Constants.WRAPPER) Wrapper<OperationLog> wrapper);
} 