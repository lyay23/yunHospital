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
import java.util.Map;

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
    public   boolean saveBatch(Collection<Scheduling> entityList) {
        try {
            System.out.println("1111");
            removeBatch(entityList);
            super.saveBatch(entityList);
        }catch (Exception ex){
            return false;
        }
        return true;
    }



    @Transactional(
            rollbackFor = {Exception.class}
    )
    public boolean removeBatch(Collection<Scheduling> entityList) {
        try {
            for (Scheduling scheduling:entityList) {
                getBaseMapper().delete(scheduling);
            }
        }catch (Exception exception){
            return  false;
        }
        return true;

    }

    //    public int addScheduling(Map<String,Object> scheduling)  {
//        String start = (String)scheduling.get("start");
//        String end = (String)scheduling.get("end");
//        String Week = (String)scheduling.get("Week");
//        String userId = (String)scheduling.get("UserId");
//        int n=0;
//        Date d1;
//        Date d2;
//        try {
//            d1 = new SimpleDateFormat("yyyy-MM-dd").parse(start);
//            d2 = new SimpleDateFormat("yyyy-MM-dd").parse(end);//定义结束日期
//            Calendar cd1 = Calendar.getInstance();//定义日期实例
//            Calendar cd2 = Calendar.getInstance();//定义日期实例
//
//            cd2.setTime(d2);//设置日期起始时间
//            cd2.add(Calendar.DATE, 1);
//            d2=cd2.getTime();
//            cd1.setTime(d1);//设置日期起始时间
//            while(cd1.getTime().before(d2)){//判断是否到结束日期
//                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//                String str = sdf.format(cd1.getTime());
//                scheduling.put("SchedDate", str);
//
//                int h=dayForWeek(str);
//                if(Week.charAt((h-1)*2)=='1')
//                {
//                    scheduling.put("Noon", "上午");
//                    n= schedulingMapper.addScheduling(scheduling);
//                }
//                if(Week.charAt((h-1)*2+1)=='1')
//                {
//                    scheduling.put("Noon", "下午");
//                    n= getBaseMapper().insert(scheduling);
//                }
//                cd1.add(Calendar.DATE, 1);//进行当前日期月份加1
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }//定义起始日期
//        return n;
//    }

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
