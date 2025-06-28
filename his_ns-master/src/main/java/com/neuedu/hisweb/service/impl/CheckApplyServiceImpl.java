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
import com.neuedu.hisweb.mapper.PatientcostsMapper;
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

    @Autowired
    private PatientcostsMapper patientcostsMapper;

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
                            // cost.setPayTime(LocalDateTime.now().toString());
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

            // Repurpose the 'position' field to store the department ID
            if (checkApply.getDeptId() != null) {
                checkApply.setPosition(String.valueOf(checkApply.getDeptId()));
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
        List<CheckApply> checkApplies = this.listByIds(ids);
        if (checkApplies == null || checkApplies.isEmpty()) {
            return false;
        }

        List<Integer> paidIds = checkApplies.stream()
                .filter(item -> item.getState() >= 2) // 已缴费
                .map(CheckApply::getId)
                .collect(Collectors.toList());

        List<Integer> unpaidIds = checkApplies.stream()
                .filter(item -> item.getState() < 2) // 未缴费
                .map(CheckApply::getId)
                .collect(Collectors.toList());

        // 1. 处理未支付的项目：直接删除
        if (!unpaidIds.isEmpty()) {
            LambdaQueryWrapper<Patientcosts> costQuery = new LambdaQueryWrapper<>();
            costQuery.in(Patientcosts::getItemID, unpaidIds).eq(Patientcosts::getItemType, 3);
            patientcostsMapper.delete(costQuery);
            this.removeByIds(unpaidIds);
        }

        // 2. 处理已支付的项目：标记为已退费
        if (!paidIds.isEmpty()) {
            // 2.1 更新 Patientcosts 记录，将其标记为已退费
            LambdaQueryWrapper<Patientcosts> costUpdateQuery = new LambdaQueryWrapper<>();
            costUpdateQuery.in(Patientcosts::getItemID, paidIds).eq(Patientcosts::getItemType, 3);
            List<Patientcosts> costsToUpdate = patientcostsMapper.selectList(costUpdateQuery);

            for (Patientcosts cost : costsToUpdate) {
                cost.setBackID(cost.getId()); // 通过设置BackID来标记为已退费
                patientcostsMapper.updateById(cost);
            }

            // 2.2 更新 CheckApply 记录的状态
            LambdaQueryWrapper<CheckApply> updateWrapper = new LambdaQueryWrapper<>();
            updateWrapper.in(CheckApply::getId, paidIds);
            CheckApply updateEntity = new CheckApply();
            updateEntity.setState(5); // 5: 已退费
            this.update(updateEntity, updateWrapper);
        }

        return true;
    }
} 