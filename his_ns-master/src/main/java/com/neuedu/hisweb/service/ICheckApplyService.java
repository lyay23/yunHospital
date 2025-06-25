package com.neuedu.hisweb.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.neuedu.hisweb.entity.CheckApply;
import com.neuedu.hisweb.entity.vo.CheckApplyVo;

import java.util.Collection;
import java.util.List;


public interface ICheckApplyService extends IService<CheckApply> {
    Page<CheckApplyVo> selectPage(Page<CheckApplyVo> page, Integer registId, Integer recordType);

    boolean addItems(List<CheckApply> items);

    boolean updateState(List<Integer> ids, Integer state);

    boolean saveOrUpdateBatch(Collection<CheckApply> entityList);
} 