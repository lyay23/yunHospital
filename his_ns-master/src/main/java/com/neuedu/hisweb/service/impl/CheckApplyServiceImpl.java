package com.neuedu.hisweb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neuedu.hisweb.entity.*;
import com.neuedu.hisweb.entity.vo.CheckApplyVo;
import com.neuedu.hisweb.mapper.CheckApplyMapper;
import com.neuedu.hisweb.service.ICheckApplyService;
import com.neuedu.hisweb.service.IFmeditemService;
import com.neuedu.hisweb.service.IInvoiceService;
import com.neuedu.hisweb.service.IPatientcostsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lynn
 * @since 2023-08-01
 */
@Service
public class CheckApplyServiceImpl extends ServiceImpl<CheckApplyMapper, CheckApply> implements ICheckApplyService {

    @Autowired
    private CheckApplyMapper checkApplyMapper;
    
    @Autowired
    private IPatientcostsService patientcostsService;

    @Autowired
    private IFmeditemService fmeditemService;

    @Autowired
    private IInvoiceService invoiceService;

    @Override
    public Page<CheckApplyVo> selectPage(Page<CheckApplyVo> page, Integer registId, Integer recordType) {
        return checkApplyMapper.selectPage(page, registId, recordType);
    }

    @Override
    @Transactional
    public boolean addItems(List<CheckApply> items) {
        for (CheckApply item : items) {
            if(item.getId() == null) { //it's a new item
                item.setName(item.getItemName());
            item.setState(1); // 1-暂存
            item.setCreationTime(LocalDateTime.now());
            //如果前端没有传入
                if (item.getCheckOperId() == null) {
                item.setCheckOperId(item.getDoctorId());
            }
                if (item.getResultOperId() == null) {
                item.setResultOperId(item.getDoctorId());
                }
            }
        }
        return saveOrUpdateBatch(items);
    }

    @Override
    @Transactional
    public boolean updateState(List<Integer> ids, Integer state) {
        if(ids==null || ids.size()==0)return false;
        List<CheckApply> list = this.listByIds(ids);

        for (CheckApply checkApply : list) {
            checkApply.setState(state);
        }
        boolean re= this.updateBatchById(list);

        if(re && state==2){//检查项目设置为已登记状态，需要写入患者费用
            List<Patientcosts> costList=new ArrayList<>();
            // 1. Collect all registIds
            Set<Integer> registIds = list.stream().map(CheckApply::getRegistId).collect(Collectors.toSet());
            if(!registIds.isEmpty()) {
                // 2. Find all active invoices for these registIds
                LambdaQueryWrapper<Invoice> invoiceQueryWrapper = new LambdaQueryWrapper<>();
                invoiceQueryWrapper.in(Invoice::getRegistID, registIds).eq(Invoice::getState, 3);
                List<Invoice> invoices = invoiceService.list(invoiceQueryWrapper);
                Map<Integer, Invoice> invoiceMap = invoices.stream().collect(Collectors.toMap(Invoice::getRegistID, Function.identity(), (existing, replacement) -> existing));


                for (CheckApply checkApply : list) {
                    LambdaQueryWrapper<Fmeditem> fmeditemLambdaQueryWrapper = new LambdaQueryWrapper<>();
                    fmeditemLambdaQueryWrapper.eq(Fmeditem::getId, checkApply.getItemId()).eq(Fmeditem::getDelMark, 1);

                    Fmeditem fmeditem = fmeditemService.getOne(fmeditemLambdaQueryWrapper);

                    if (fmeditem != null) {
                        // 3. Get invoice from map
                        Invoice invoice = invoiceMap.get(checkApply.getRegistId());
                        if (invoice != null) {
                            Patientcosts cost = new Patientcosts();
                            Integer num = checkApply.getNum();
                            cost.setAmount(1.0 * (num != null ? num : 1));
                            cost.setCreateOperID(checkApply.getDoctorId());//操作员id
                            cost.setCreatetime(LocalDateTime.now().toString());
                            cost.setPayTime(LocalDateTime.now().toString());
                            cost.setDeptID(fmeditem.getDeptID());
                            cost.setFeeType(fmeditem.getExpClassID());//费用类别
                            cost.setItemID(fmeditem.getId());
                            cost.setItemType(1);//1检查
                            cost.setName(fmeditem.getItemName());
                            cost.setPrice(fmeditem.getPrice().doubleValue());
                            cost.setRegistID(checkApply.getRegistId());
                            cost.setInvoiceID(invoice.getId());
                            cost.setRegisterID(invoice.getUserID());

                            costList.add(cost);
                        }
                    }
                }

                if (costList.size() > 0) {
                    patientcostsService.saveBatch(costList);
                }
            }
        } else if (re && state == 0) { // 作废操作
            List<Patientcosts> costsToCancel = new ArrayList<>();
            for (CheckApply checkApply : list) {
                LambdaQueryWrapper<Patientcosts> costQuery = new LambdaQueryWrapper<>();
                costQuery.eq(Patientcosts::getRegistID, checkApply.getRegistId())
                        .eq(Patientcosts::getItemID, checkApply.getItemId())
                        .isNull(Patientcosts::getBackID)
                        .orderByDesc(Patientcosts::getId)
                        .last("LIMIT 1");

                Patientcosts existingCost = patientcostsService.getOne(costQuery);

                if (existingCost != null) {
                    Patientcosts cancellation = new Patientcosts();
                    cancellation.setAmount(-existingCost.getAmount());
                    cancellation.setBackID(existingCost.getId());
                    cancellation.setCreatetime(LocalDateTime.now().toString());
                    cancellation.setPayTime(LocalDateTime.now().toString());
                    cancellation.setCreateOperID(checkApply.getDoctorId()); 
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
            if (!costsToCancel.isEmpty()) {
                patientcostsService.saveBatch(costsToCancel);
            }
        }


        return re;
    }

    @Override
    @Transactional
    public boolean saveOrUpdateBatch(Collection<CheckApply> entityList) {
        for (CheckApply checkApply : entityList) {
            // Ensure the name is set from itemName
            if (checkApply.getName() == null && checkApply.getItemName() != null) {
                checkApply.setName(checkApply.getItemName());
            }
            // Set creation time for new entities
            if (checkApply.getId() == null) {
                checkApply.setCreationTime(LocalDateTime.now());
                // If it's a new item, also set operator IDs from doctor ID, if they are not provided
                if (checkApply.getCheckOperId() == null) {
                    checkApply.setCheckOperId(checkApply.getDoctorId());
                }
                if (checkApply.getResultOperId() == null) {
                    checkApply.setResultOperId(checkApply.getDoctorId());
                }
            }
        }
        return super.saveOrUpdateBatch(entityList);
    }

    @Override
    @Transactional
    public boolean delete(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }
        LambdaQueryWrapper<CheckApply> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(CheckApply::getId, ids)
                .and(wrapper -> wrapper.eq(CheckApply::getState, 1)
                        .or()
                        .eq(CheckApply::getState, 0));

        List<CheckApply> list = this.list(queryWrapper);

        if (list.size() != ids.size()) {
            // Not all items are in a deletable state
            return false;
        }

        return this.removeByIds(ids);
    }
} 