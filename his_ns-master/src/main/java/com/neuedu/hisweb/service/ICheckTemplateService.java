package com.neuedu.hisweb.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.neuedu.hisweb.entity.CheckTemplate;
import com.neuedu.hisweb.entity.vo.CheckTemplateItemVo;

public interface ICheckTemplateService extends IService<CheckTemplate> {
    boolean saveTemplate(CheckTemplateItemVo templateVo);
} 