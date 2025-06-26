package com.neuedu.hisweb.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neuedu.hisweb.entity.CheckRelation;
import com.neuedu.hisweb.entity.CheckTemplate;
import com.neuedu.hisweb.entity.Fmeditem;
import com.neuedu.hisweb.entity.vo.CheckTemplateItemVo;
import com.neuedu.hisweb.mapper.CheckTemplateMapper;
import com.neuedu.hisweb.service.ICheckRelationService;
import com.neuedu.hisweb.service.ICheckTemplateService;
import com.neuedu.hisweb.service.IFmeditemService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

@Service
public class CheckTemplateServiceImpl extends ServiceImpl<CheckTemplateMapper, CheckTemplate> implements ICheckTemplateService {

    @Autowired
    private ICheckRelationService checkRelationService;

    @Autowired
    private IFmeditemService fmeditemService;

    @Override
    @Transactional
    public boolean saveTemplate(CheckTemplateItemVo templateVo) {
        // 1. 保存CheckTemplate主表
        templateVo.setCreationTime(LocalDateTime.now());
        templateVo.setUserId(templateVo.getDoctorId());
        
        // Convert scope "personal" to "3"
        if ("personal".equalsIgnoreCase(templateVo.getScope())) {
            templateVo.setScope("3");
        }
        
        boolean result = save(templateVo);
        if (!result) {
            return false;
        }

        // 2. 准备CheckRelation关联表数据
        List<Integer> itemIds = templateVo.getItemIds();
        if (itemIds != null && !itemIds.isEmpty()) {
            List<CheckRelation> relations = itemIds.stream().map(itemId -> {
                CheckRelation relation = new CheckRelation();
                relation.setCheckTempId(templateVo.getId());
                relation.setCheckProjId(itemId);
                return relation;
            }).collect(Collectors.toList());

            // 3. 批量保存关联表数据
            return checkRelationService.saveBatch(relations);
        }

        return true;
    }

    @Override
    public CheckTemplateItemVo getTemplateWithItems(Integer id) {
        CheckTemplate checkTemplate = getById(id);
        if (checkTemplate == null) {
            return null;
        }

        CheckTemplateItemVo vo = new CheckTemplateItemVo();
        BeanUtils.copyProperties(checkTemplate, vo);

        LambdaQueryWrapper<CheckRelation> relationWrapper = new LambdaQueryWrapper<>();
        relationWrapper.eq(CheckRelation::getCheckTempId, id);
        List<CheckRelation> relations = checkRelationService.list(relationWrapper);

        if (relations != null && !relations.isEmpty()) {
            List<Integer> itemIds = relations.stream()
                    .map(CheckRelation::getCheckProjId)
                    .collect(Collectors.toList());
            
            if(!itemIds.isEmpty()) {
                List<Fmeditem> items = fmeditemService.listByIds(itemIds);
                vo.setItems(items);
            }
        }

        return vo;
    }
} 