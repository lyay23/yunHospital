package com.neuedu.hisweb.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.hisweb.entity.DrugsTemplate;
import com.neuedu.hisweb.entity.Prescription;
import com.neuedu.hisweb.entity.PrescriptionDetailed;
import com.neuedu.hisweb.entity.vo.DrugsTemplateVo;
import com.neuedu.hisweb.entity.vo.PrescriptionVo;
import com.neuedu.hisweb.mapper.PrescriptionMapper;
import com.neuedu.hisweb.service.IDrugsTemplateService;
import com.neuedu.hisweb.service.IPrescriptionDetailedService;
import com.neuedu.hisweb.service.IPrescriptionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import com.neuedu.hisweb.entity.*;
import com.neuedu.hisweb.service.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.util.Map;
import java.util.function.Function;
import java.util.Set;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lynn
 * @since 2024-07-26
 */
@Service
@Slf4j
public class PrescriptionServiceImpl extends ServiceImpl<PrescriptionMapper, Prescription> implements IPrescriptionService {
    @Autowired
    private PrescriptionMapper prescriptionMapper;
    @Autowired
    private IDrugsTemplateService drugsTemplateService;
    @Autowired
    private IPrescriptionDetailedService prescriptionDetailedService;
    @Autowired
    private IPatientcostsService patientcostsService;
    @Autowired
    private IDrugsService drugsService;

    @Autowired
    private IInvoiceService invoiceService;

    @Autowired
    private IRegisterService registerService;


    @Override
    public Page<PrescriptionVo> selectPage(Page<PrescriptionVo> page, Integer registId) {
        return prescriptionMapper.selectPage(page,registId);
    }

    @Override
    @Transactional
    public Boolean saveByTemplate(PrescriptionVo prescriptionVo) {
        // 1. 根据模板ID查询模板详情
        List<DrugsTemplateVo> details = drugsTemplateService.selectDetail(prescriptionVo.getTemplateId().toString());
        if(details == null || details.isEmpty()){
            log.error("未找到模板详情或模板为空, TemplateId: {}", prescriptionVo.getTemplateId());
            return false;
        }

        // 2. 准备处方详情列表
        List<PrescriptionDetailed> list = new ArrayList<>();
        details.forEach(detail -> {
            PrescriptionDetailed prescriptionDetailed = new PrescriptionDetailed();
            // 手动映射字段
            prescriptionDetailed.setPrescriptionId(prescriptionVo.getPrescriptionId()); // 关联到当前处方
            prescriptionDetailed.setDrugsId(detail.getDrugsID());
            prescriptionDetailed.setDrugsUsage(detail.getDrugsUsage());
            prescriptionDetailed.setDosage(detail.getDosage());
            prescriptionDetailed.setFrequency(detail.getFrequency());
            // 可以在这里设置默认值
            prescriptionDetailed.setAmount(BigDecimal.ONE);
            prescriptionDetailed.setState(1); // 1-暂存
            list.add(prescriptionDetailed);
        });

        // 3. 批量保存处方详情
        return prescriptionDetailedService.saveBatch(list);
    }

    @Override
    @Transactional
    public boolean updateState(List<Integer> ids, Integer state) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }
        List<Prescription> list = this.listByIds(ids);
        for(Prescription p : list){
            p.setPrescriptionState(state);
        }

        boolean re= this.updateBatchById(list);

        if(re && state==2){//开立
            List<Patientcosts> costList=new ArrayList<>();
            // 1. Collect all registIds
            Set<Integer> registIds = list.stream().map(Prescription::getRegistId).collect(Collectors.toSet());
            // 2. Find all active invoices for these registIds
            LambdaQueryWrapper<Invoice> invoiceQueryWrapper = new LambdaQueryWrapper<>();
            invoiceQueryWrapper.in(Invoice::getRegistID, registIds).eq(Invoice::getState, 3);
            List<Invoice> invoices = invoiceService.list(invoiceQueryWrapper);
            Map<Integer, Invoice> invoiceMap = invoices.stream().collect(Collectors.toMap(Invoice::getRegistID, Function.identity(), (existing, replacement) -> existing));

            LambdaQueryWrapper<PrescriptionDetailed> detailedLambdaQueryWrapper=new LambdaQueryWrapper<>();
            detailedLambdaQueryWrapper.in(PrescriptionDetailed::getPrescriptionId,ids);

            List<PrescriptionDetailed> detailedList= prescriptionDetailedService.list(detailedLambdaQueryWrapper);
            if(detailedList!=null && detailedList.size()>0){
                for(PrescriptionDetailed pd:detailedList){
                    pd.setState(2);
                }
                prescriptionDetailedService.updateBatchById(detailedList);

                for(Prescription p : list) {
                    Invoice invoice = invoiceMap.get(p.getRegistId());
                    Register register = registerService.getById(p.getRegistId());
                    if (invoice != null && register != null) {
                        for (PrescriptionDetailed pd : detailedList) {
                            if(pd.getPrescriptionId().equals(p.getId())) {
                                Drugs drugs = drugsService.getById(pd.getDrugsId());
                                Patientcosts cost = new Patientcosts();
                                cost.setAmount(pd.getAmount().doubleValue());
                                cost.setCreateOperID(p.getUserId());//操作员id
                                cost.setCreatetime(LocalDateTime.now().toString());
                                cost.setDeptID(register.getDeptID());
                                cost.setFeeType(register.getSettleID());//费用类别
                                cost.setItemID(drugs.getId());
                                cost.setItemType(2);//2药品
                                cost.setName(drugs.getDrugsName());
                                cost.setPrice(drugs.getDrugsPrice().doubleValue());
                                cost.setRegistID(p.getRegistId());
                                cost.setInvoiceID(invoice.getId());
                                cost.setRegisterID(invoice.getUserID());
                                costList.add(cost);
                            }
                        }
                    }
                }
            }
            if (costList.size() > 0) {
                patientcostsService.saveBatch(costList);
            }
        }else if(re && state==3){//作废
            List<Patientcosts> costsToCancel = new ArrayList<>();
            for (Prescription p : list) {
                LambdaQueryWrapper<PrescriptionDetailed> detailedLambdaQueryWrapper=new LambdaQueryWrapper<>();
                detailedLambdaQueryWrapper.in(PrescriptionDetailed::getPrescriptionId,p.getId());
                List<PrescriptionDetailed> detailedList= prescriptionDetailedService.list(detailedLambdaQueryWrapper);
                if(detailedList!=null && detailedList.size()>0) {
                    for (PrescriptionDetailed pd : detailedList) {
                        pd.setState(0);
                    }
                    prescriptionDetailedService.updateBatchById(detailedList);
                    for (PrescriptionDetailed pd : detailedList) {
                        LambdaQueryWrapper<Patientcosts> costQuery = new LambdaQueryWrapper<>();
                        costQuery.eq(Patientcosts::getRegistID, p.getRegistId())
                                .eq(Patientcosts::getItemID, pd.getDrugsId())
                                .eq(Patientcosts::getItemType, 2)
                                .isNull(Patientcosts::getBackID)
                                .orderByDesc(Patientcosts::getId)
                                .last("LIMIT 1");
                        Patientcosts existingCost = patientcostsService.getOne(costQuery);
                        if (existingCost != null) {
                            Patientcosts cancellation = new Patientcosts();
                            cancellation.setAmount(-existingCost.getAmount());
                            cancellation.setBackID(existingCost.getId());
                            cancellation.setCreatetime(LocalDateTime.now().toString());
                            cancellation.setCreateOperID(p.getUserId());
                            cancellation.setRegisterID(existingCost.getRegisterID());
                            cancellation.setDeptID(existingCost.getDeptID());
                            cancellation.setFeeType(existingCost.getFeeType());
                            cancellation.setInvoiceID(existingCost.getInvoiceID());
                            cancellation.setItemID(existingCost.getItemID());
                            cancellation.setItemType(existingCost.getItemType());
                            cancellation.setName(existingCost.getName());
                            cancellation.setPrice(existingCost.getPrice());
                            cancellation.setRegistID(existingCost.getRegistID());

                            costsToCancel.add(cancellation);
                        }
                    }
                }
            }
            if (!costsToCancel.isEmpty()) {
                patientcostsService.saveBatch(costsToCancel);
            }
        }

        return re;
    }
} 