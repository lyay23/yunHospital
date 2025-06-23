package com.neuedu.hisweb.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neuedu.hisweb.entity.CheckRelation;
import com.neuedu.hisweb.entity.vo.FmeditemVo;
import com.neuedu.hisweb.mapper.CheckRelationMapper;
import com.neuedu.hisweb.service.ICheckRelationService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CheckRelationServiceImpl extends ServiceImpl<CheckRelationMapper, CheckRelation> implements ICheckRelationService {

    @Override
    public List<FmeditemVo> findItemsByTemplateId(Integer templateId) {
        return getBaseMapper().findItemsByTemplateId(templateId);
    }
} 