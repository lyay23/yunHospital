package com.neuedu.hisweb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neuedu.hisweb.entity.*;
import com.neuedu.hisweb.entity.vo.CheckApplyVo;
import com.neuedu.hisweb.entity.vo.RegisterVo;
import com.neuedu.hisweb.entity.vo.CheckResultVo;
import com.neuedu.hisweb.mapper.CheckApplyMapper;
import com.neuedu.hisweb.service.*;
import com.neuedu.hisweb.mapper.PatientcostsMapper;
import com.neuedu.hisweb.mapper.InvoiceMapper;
import com.neuedu.hisweb.mapper.MedicalResultMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.math.BigDecimal;
import com.neuedu.hisweb.utils.UserUtils;

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

    @Autowired
    private IRegisterService registerService;
    @Autowired
    private IUserService userService;
    @Autowired
    private IDepartmentService departmentService;
    @Autowired
    private ISettlecategoryService settlecategoryService;
    @Autowired
    private IMedicalCardService medicalCardService;
    @Autowired
    private InvoiceMapper invoiceMapper;

    @Autowired
    private IMedicalResultService medicalResultService;

    @Autowired
    private MedicalResultMapper medicalResultMapper;

    @Override
    public Page<CheckApplyVo> selectPage(Page<CheckApplyVo> page, CheckApplyVo checkApplyVo) {
        return checkApplyMapper.selectPage(page,checkApplyVo);
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

    @Override
    public Map<String, Object> getCheckApplyDetails(Integer registId) {
        // 1. 获取挂号信息
        Register register = registerService.getById(registId);
        if (register == null) return null;

        RegisterVo registerVo = new RegisterVo();
        BeanUtils.copyProperties(register, registerVo);

        // 2. 丰富挂号信息
        // 2.1 医生姓名
        if (registerVo.getUserID() != null) {
            User doctor = userService.getById(registerVo.getUserID());
            if(doctor!=null) registerVo.setDoctorName(doctor.getRealName());
        }
        // 2.2 科室名称
        if (registerVo.getDeptID() != null) {
            Department department = departmentService.getById(registerVo.getDeptID());
            if(department!=null) registerVo.setDeptName(department.getDeptName());
        }
        // 2.3 结算类别
        if(registerVo.getSettleID()!=null){
            Settlecategory settlecategory = settlecategoryService.getById(registerVo.getSettleID());
            if(settlecategory!=null) {
                registerVo.setSettleCategoryName(settlecategory.getSettleName());
            } else {
                registerVo.setSettleCategoryName("线下"); // 默认值
            }
        } else {
            registerVo.setSettleCategoryName("线下"); // 默认值
        }
        // 2.4 患者姓名
        if(register.getMedicalCardId() != null){
            MedicalCard medicalCard = medicalCardService.getById(register.getMedicalCardId());
            if(medicalCard != null){
                registerVo.setRealName(medicalCard.getRealname());
            }
        }
        // 2.5 发票号
        Invoice invoice = invoiceMapper.selectByRegistId(registId);
        if(invoice != null) {
            registerVo.setInvoiceNum(invoice.getInvoiceNum());
        }


        // 3. 获取并丰富检查申请列表
        LambdaQueryWrapper<CheckApply> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CheckApply::getRegistId, registId);
        queryWrapper.in(CheckApply::getState, 0, 2, 4, 5); // 查询已作废、已开立、已登记和已执行完的项目
        List<CheckApply> checkApplies = this.list(queryWrapper);
        List<CheckApplyVo> checkApplyVos = new ArrayList<>();

        if (checkApplies != null && !checkApplies.isEmpty()) {
            // 将第一个检查单的状态作为总状态
            registerVo.setState(checkApplies.get(0).getState());
            for (CheckApply checkApply : checkApplies) {
                CheckApplyVo vo = new CheckApplyVo();
                BeanUtils.copyProperties(checkApply, vo);
                if(checkApply.getItemId() != null){
                    Fmeditem fmeditem = fmeditemService.getById(checkApply.getItemId());
                    if(fmeditem!=null){
                        vo.setItemName(fmeditem.getItemName());
                        vo.setPrice(fmeditem.getPrice());
                        BigDecimal num = new BigDecimal(vo.getNum() == null ? 1 : vo.getNum());
                        vo.setTotalAmount(fmeditem.getPrice().multiply(num));
                    }
                }
                checkApplyVos.add(vo);
            }
        }


        // 4. 组装返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("checkApply", registerVo);
        result.put("checkDetail", checkApplyVos);
        return result;
    }

    @Override
    public boolean executeCheck(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }
        Integer userId = UserUtils.getLoginUser().getId();
        List<CheckApply> checkApplies = this.listByIds(ids);
        for (CheckApply checkApply : checkApplies) {
            // 只有已开立的项目可以执行
            if (checkApply.getState() == 2) {
                checkApply.setState(4); // 4: 已登记
                checkApply.setCheckOperId(userId);
                checkApply.setCheckTime(LocalDateTime.now());
            }
        }
        return this.updateBatchById(checkApplies);
    }

    @Override
    public boolean cancelExecuteCheck(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }
        List<CheckApply> checkApplies = this.listByIds(ids);
        for (CheckApply checkApply : checkApplies) {
            // 只有已登记的项目可以取消
            if (checkApply.getState() == 4) {
                checkApply.setState(0); // 0: 已作废
                checkApply.setCheckOperId(null);
                checkApply.setCheckTime(null);
            }
        }
        return this.updateBatchById(checkApplies);
    }

    @Override
    @Transactional
    public boolean saveResult(CheckResultVo resultVo) {
        List<Integer> ids = resultVo.getCheckApplyIds();
        if (ids == null || ids.isEmpty()) {
            return false;
        }

        CheckApply firstItem = this.getById(ids.get(0));
        if (firstItem == null) {
            return false;
        }
        Integer recordType = firstItem.getRecordType();

        for (Integer checkApplyId : ids) {
            MedicalResult mr = new MedicalResult();
            mr.setItemId(checkApplyId);
            mr.setRegistId(resultVo.getRegistId());
            mr.setResultDesc(resultVo.getResultDesc());
            mr.setResultImages(resultVo.getResultImages());
            mr.setItemType(recordType);
            mr.setCreateTime(LocalDateTime.now());
            medicalResultMapper.insert(mr);
        }

        UpdateWrapper<CheckApply> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in("ID", ids).set("State", 5);
        this.update(updateWrapper);

        return true;
    }
}