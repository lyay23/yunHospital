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
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.neuedu.hisweb.entity.*;
import com.neuedu.hisweb.mapper.InvoiceMapper;
import com.neuedu.hisweb.mapper.RegisterMapper;
import com.neuedu.hisweb.utils.Utils;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
    
    @Autowired
    private InvoiceMapper invoiceMapper;

    @Autowired
    private RegisterMapper registerMapper;
    
    @Override
    public List<PatientCostVo> selectPatientCost(String keyword, Integer itemType) {
        List<PatientCostVo> list = mapper.selectPatientCost(keyword, itemType);
        Set<Integer> refundedIds = list.stream()
                .map(PatientCostVo::getBackID)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        for(PatientCostVo vo : list){
            if (refundedIds.contains(vo.getId()) || vo.getBackID() != null) {
                vo.setStatus("已退费");
            } else if(vo.getPayTime() != null){
                vo.setStatus("已收费");
            }else{
                vo.setStatus("未收费");
            }
        }
        return list;
    }

    @Override
    @Transactional
    public boolean doPay(Integer registId) {
        // 1. 找到所有未支付的项目
        LambdaQueryWrapper<Patientcosts> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Patientcosts::getRegistID, registId).isNull(Patientcosts::getPayTime);
        List<Patientcosts> unpaidItems = mapper.selectList(queryWrapper);

        if (unpaidItems.isEmpty()) {
            return true; // 没有需要支付的项目
        }

        // 2. 计算总金额
        double totalAmount = unpaidItems.stream()
                .mapToDouble(item -> item.getPrice() * item.getAmount())
                .sum();

        // 3. 创建发票
        Invoice invoice = new Invoice();
        invoice.setInvoiceNum(Utils.getInvoiceNum());
        invoice.setMoney(totalAmount);
        invoice.setState(3); // 正常状态
        invoice.setCreationTime(LocalDateTime.now().toString());
        invoice.setUserID(1); // 模拟收费员ID
        invoice.setRegistID(registId);
        invoice.setFeeType(1); // 费用类型,可根据实际情况调整
        invoiceMapper.insert(invoice);

        // 4. 更新所有项目的支付时间和发票ID
        for (Patientcosts item : unpaidItems) {
            item.setPayTime(LocalDateTime.now().toString());
            item.setInvoiceID(invoice.getId());
            mapper.updateById(item);
        }

        // 5. 更新挂号表状态为已缴费
        registerMapper.updateVisitState(registId, 2);

        return true;
    }
}
