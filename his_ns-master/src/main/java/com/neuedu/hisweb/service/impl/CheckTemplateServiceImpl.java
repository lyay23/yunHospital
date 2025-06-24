package com.neuedu.hisweb.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neuedu.hisweb.entity.CheckRelation;
import com.neuedu.hisweb.entity.CheckTemplate;
import com.neuedu.hisweb.entity.vo.CheckTemplateItemVo;
import com.neuedu.hisweb.mapper.CheckTemplateMapper;
import com.neuedu.hisweb.service.ICheckRelationService;
import com.neuedu.hisweb.service.ICheckTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CheckTemplateServiceImpl extends ServiceImpl<CheckTemplateMapper, CheckTemplate> implements ICheckTemplateService {

    @Autowired
    private ICheckRelationService checkRelationService;

    @Override
    @Transactional
    public boolean saveTemplate(CheckTemplateItemVo templateVo) {
        // 1. 保存CheckTemplate主表
        templateVo.setCreationTime(LocalDateTime.now());
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
} 