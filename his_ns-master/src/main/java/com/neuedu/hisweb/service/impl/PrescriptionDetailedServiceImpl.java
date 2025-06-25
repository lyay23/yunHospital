package com.neuedu.hisweb.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.hisweb.entity.PrescriptionDetailed;
import com.neuedu.hisweb.entity.vo.PrescriptionDetailedVo;
import com.neuedu.hisweb.mapper.PrescriptionDetailedMapper;
import com.neuedu.hisweb.service.IPrescriptionDetailedService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lynn
 * @since 2024-07-26
 */
@Service
public class PrescriptionDetailedServiceImpl extends ServiceImpl<PrescriptionDetailedMapper, PrescriptionDetailed> implements IPrescriptionDetailedService {

    @Autowired
    private PrescriptionDetailedMapper prescriptionDetailedMapper;

    @Override
    public Page<PrescriptionDetailedVo> selectPage(Page<PrescriptionDetailedVo> page, Integer prescriptionId) {
        return prescriptionDetailedMapper.selectPage(page, prescriptionId);
    }
} 