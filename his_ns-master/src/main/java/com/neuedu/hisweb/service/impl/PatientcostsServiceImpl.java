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
import com.neuedu.hisweb.service.IInvoiceService;
import com.neuedu.hisweb.service.IRegisterService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import com.neuedu.hisweb.entity.Invoice;

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
    
    @Autowired
    private IRegisterService registerService;

    @Autowired
    private IInvoiceService invoiceService;
    
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
    public boolean doPay(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }
        Integer registId = ids.get(0);
        LambdaQueryWrapper<Patientcosts> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Patientcosts::getRegistID, registId).isNull(Patientcosts::getPayTime);
        List<Patientcosts> unpaidItems = mapper.selectList(queryWrapper);

        if(unpaidItems.isEmpty()){
            return true;
        }

        BigDecimal totalAmount = unpaidItems.stream()
                .map(item -> new BigDecimal(String.valueOf(item.getPrice())).multiply(new BigDecimal(String.valueOf(item.getAmount()))))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Register register = registerService.getById(registId);
        if(register == null) return false;
        register.setVisitState(2);
        registerService.updateById(register);

        Invoice invoice = new Invoice();
        invoice.setInvoiceNum(String.valueOf(System.currentTimeMillis()));
        invoice.setMoney(totalAmount.doubleValue());
        invoice.setState(1);
        invoice.setCreationTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        invoice.setUserID(register.getRegisterID());
        invoice.setRegistID(register.getId());
        invoice.setFeeType(1); // 假设1为线下支付
        invoiceService.save(invoice);

        for (Patientcosts item : unpaidItems) {
            item.setPayTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            item.setInvoiceID(invoice.getId());
            mapper.updateById(item);
        }

        return true;
    }
}
