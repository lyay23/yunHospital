package com.neuedu.hisweb.service.impl;

import com.neuedu.hisweb.entity.JsonResult;
import com.neuedu.hisweb.entity.Patientcosts;
import com.neuedu.hisweb.mapper.PatientcostsMapper;
import com.neuedu.hisweb.service.IPatientcostsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import com.neuedu.hisweb.entity.vo.PatientCostVo;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lynn
 * @since 2023-08-09
 */
@Service
public class PatientcostsServiceImpl extends ServiceImpl<PatientcostsMapper, Patientcosts> implements IPatientcostsService {
    @Autowired
    private PatientcostsMapper mapper;
    
    @Override
    public List<PatientCostVo> selectPatientCost(String keyword, Integer itemType) {
        List<PatientCostVo> list = mapper.selectPatientCost(keyword, itemType);
        Set<Integer> refundedIds = list.stream()
                .map(PatientCostVo::getBackID)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        for(PatientCostVo vo : list){
            //设置状态
            if (refundedIds.contains(vo.getId()) || vo.getBackID() != null) {
                vo.setStatus("已退费");
            } else if(vo.getInvoiceID() != null){
                vo.setStatus("已收费");
            }else{
                vo.setStatus("划价");
            }
        }
        return list;
    }
}
