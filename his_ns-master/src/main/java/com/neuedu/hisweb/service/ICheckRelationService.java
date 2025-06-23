package com.neuedu.hisweb.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.neuedu.hisweb.entity.CheckRelation;
import com.neuedu.hisweb.entity.vo.FmeditemVo;

import java.util.List;

public interface ICheckRelationService extends IService<CheckRelation> {
    List<FmeditemVo> findItemsByTemplateId(Integer templateId);
} 