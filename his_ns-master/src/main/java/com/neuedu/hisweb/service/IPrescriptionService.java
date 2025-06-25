package com.neuedu.hisweb.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.hisweb.entity.Prescription;
import com.baomidou.mybatisplus.extension.service.IService;
import com.neuedu.hisweb.entity.vo.PrescriptionVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lynn
 * @since 2024-07-26
 */
public interface IPrescriptionService extends IService<Prescription> {
    Page<PrescriptionVo> selectPage(Page<PrescriptionVo> page, Integer registId);

    Boolean saveByTemplate(PrescriptionVo prescriptionVo);

    boolean updateState(List<Integer> ids, Integer state);
} 