package com.neuedu.hisweb.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.hisweb.entity.Drugs;
import com.baomidou.mybatisplus.extension.service.IService;

public interface IDrugsService extends IService<Drugs> {
    Page<Drugs> selectPage(Page<Drugs> page, String keyword);
} 