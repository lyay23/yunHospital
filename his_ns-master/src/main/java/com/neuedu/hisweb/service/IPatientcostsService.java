package com.neuedu.hisweb.service;

import com.neuedu.hisweb.entity.Patientcosts;
import com.baomidou.mybatisplus.extension.service.IService;
import com.neuedu.hisweb.entity.vo.PatientCostVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lynn
 * @since 2023-08-09
 */
public interface IPatientcostsService extends IService<Patientcosts> {
    List<PatientCostVo> selectPatientCost(String keyword, Integer itemType);

    boolean doPay(Integer registId);
}
