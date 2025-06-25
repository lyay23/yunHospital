package com.neuedu.hisweb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.hisweb.entity.DrugsTemplate;
import com.neuedu.hisweb.entity.vo.DrugsTemplateVo;
import com.neuedu.hisweb.mapper.DrugsTemplateMapper;
import com.neuedu.hisweb.service.IDrugsTemplateService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lynn
 * @since 2024-07-27
 */
@Service
public class DrugsTemplateServiceImpl extends ServiceImpl<DrugsTemplateMapper, DrugsTemplate> implements IDrugsTemplateService {

    @Autowired
    private DrugsTemplateMapper drugsTemplateMapper;

    @Override
    public Page<DrugsTemplate> selectPage(Page<DrugsTemplate> page, String keyword, String scope, Integer userId) {
        QueryWrapper<DrugsTemplate> wrapper = new QueryWrapper<>();
        // 模板名称关键字
        if (StringUtils.isNotBlank(keyword)) {
            wrapper.like("Name", keyword);
        }

        // 根据范围进行查询
        if (StringUtils.isNotBlank(scope) && userId != null) {
            // 使用内嵌的查询来组合 OR 条件
            wrapper.and(qw -> qw.eq("Scope", "3") // 全院
                    .or(wq -> wq.eq("Scope", "1").eq("UserID", userId)) // 个人
            );
        }

        wrapper.eq("DelMark",1); // 未删除
        return drugsTemplateMapper.selectPage(page, wrapper);
    }

    @Override
    public List<DrugsTemplateVo> selectDetail(String templateId) {
        return drugsTemplateMapper.selectDetail(templateId);
    }
} 