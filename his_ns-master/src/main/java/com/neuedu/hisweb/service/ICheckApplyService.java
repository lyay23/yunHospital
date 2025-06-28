package com.neuedu.hisweb.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.neuedu.hisweb.entity.CheckApply;
import com.neuedu.hisweb.entity.vo.CheckApplyVo;
import com.neuedu.hisweb.entity.vo.CheckResultVo;

import java.util.Collection;
import java.util.List;
import java.util.Map;


public interface ICheckApplyService extends IService<CheckApply> {
    Page<CheckApplyVo> selectPage(Page<CheckApplyVo> page, Integer registId, Integer recordType);

    boolean addItems(List<CheckApply> items);

    boolean updateState(List<Integer> ids, Integer state);

    boolean saveOrUpdateBatch(Collection<CheckApply> entityList);

    boolean delete(List<Integer> ids);

    Map<String, Object> getCheckApplyDetails(Integer registId);

    boolean executeCheck(List<Integer> ids);

    boolean cancelExecuteCheck(List<Integer> ids);

    boolean saveResult(CheckResultVo resultVo);
} 