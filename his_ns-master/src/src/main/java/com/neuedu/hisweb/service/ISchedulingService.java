package com.neuedu.hisweb.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.hisweb.entity.Scheduling;
import com.baomidou.mybatisplus.extension.service.IService;
import com.neuedu.hisweb.entity.vo.SchedulingVo;

import java.util.Collection;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lynn
 * @since 2023-07-31
 */
public interface ISchedulingService extends IService<Scheduling> {
    Page<SchedulingVo> selectPage(Page<SchedulingVo> page, String keyword, String deptId, String userId,String regLevel,String noon,String start, String end);
}
