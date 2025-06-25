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
    public boolean updateState(List<Integer> ids, Integer state) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }
        List<Prescription> list = ids.stream().map(id -> {
            Prescription prescription = new Prescription();
            prescription.setId(id);
            prescription.setPrescriptionState(state);
            return prescription;
        }).collect(Collectors.toList());
        return this.updateBatchById(list);
    }
} 