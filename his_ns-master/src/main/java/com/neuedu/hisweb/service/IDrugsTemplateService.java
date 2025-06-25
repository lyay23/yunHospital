package com.neuedu.hisweb.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.hisweb.entity.DrugsTemplate;
import com.baomidou.mybatisplus.extension.service.IService;
import com.neuedu.hisweb.entity.vo.DrugsTemplateVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lynn
 * @since 2024-07-27
 */
public interface IDrugsTemplateService extends IService<DrugsTemplate> {
    Page<DrugsTemplate> selectPage(Page<DrugsTemplate> page, String keyword, String scope, Integer userId);

    List<DrugsTemplateVo> selectDetail(String templateId);
} 