package com.neuedu.hisweb.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.hisweb.entity.PrescriptionDetailed;
import com.baomidou.mybatisplus.extension.service.IService;
import com.neuedu.hisweb.entity.vo.PrescriptionDetailedVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lynn
 * @since 2024-07-26
 */
public interface IPrescriptionDetailedService extends IService<PrescriptionDetailed> {

    Page<PrescriptionDetailedVo> selectPage(Page<PrescriptionDetailedVo> page, Integer prescriptionId);
} 