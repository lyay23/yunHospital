package com.neuedu.hisweb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.hisweb.entity.Scheduling;
import com.neuedu.hisweb.entity.vo.SchedulingVo;
import com.neuedu.hisweb.mapper.SchedulingMapper;
import com.neuedu.hisweb.service.ISchedulingService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lynn
 * @since 2023-07-31
 */
@Service
public class SchedulingServiceImpl extends ServiceImpl<SchedulingMapper, Scheduling> implements ISchedulingService {

    @Override
    public Page<SchedulingVo> selectPage(Page<SchedulingVo> page, String keyword, String deptId, String userId,String regLevel,String noon,String start, String end) {
        return baseMapper.selectPage(page,keyword,deptId,userId,regLevel,noon,start,end);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public boolean saveBatch(Collection<Scheduling> entityList) {
        try {
            // 先删除已存在的排班
            if (entityList != null && !entityList.isEmpty()) {
                Scheduling first = entityList.iterator().next();
                List<Date> dates = entityList.stream().map(Scheduling::getSchedDate).distinct().collect(Collectors.toList());

                LambdaQueryWrapper<Scheduling> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(Scheduling::getUserID, first.getUserID())
                        .eq(Scheduling::getDeptID, first.getDeptID())
                        .eq(Scheduling::getNoon, first.getNoon())
                        .in(Scheduling::getSchedDate, dates);
                baseMapper.delete(wrapper);
            }
            // 批量插入新的排班
            return super.saveBatch(entityList);
        }catch (Exception ex){
            // 在生产环境中，这里应该使用日志框架记录异常
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * 判断当前日期是星期几
     *
     * @param pTime 修要判断的时间
     * @return dayForWeek 判断结果
     * @Exception 发生异常
     */
    public static int dayForWeek(String pTime) throws Exception {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTime(format.parse(pTime));
        int dayForWeek = 0;
        if(c.get(Calendar.DAY_OF_WEEK) == 1){
            dayForWeek = 7;
        }else{
            dayForWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
        }
        return dayForWeek;
    }
}
