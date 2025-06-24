package com.neuedu.hisweb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neuedu.hisweb.entity.CheckApply;
import com.neuedu.hisweb.entity.vo.CheckApplyVo;
import com.neuedu.hisweb.mapper.CheckApplyMapper;
import com.neuedu.hisweb.service.ICheckApplyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CheckApplyServiceImpl extends ServiceImpl<CheckApplyMapper, CheckApply> implements ICheckApplyService {

    @Override
    public Page<CheckApplyVo> selectPage(Page<CheckApplyVo> page, Integer registId, Integer recordType) {
        return getBaseMapper().selectPage(page, registId, recordType);
    }

    @Override
    @Transactional
    public boolean addItems(List<CheckApply> items) {
        for (CheckApply item : items) {
            if(item.getId() == null) { //it's a new item
                item.setName(item.getItemName());
                item.setState(1); // 1-暂存
                item.setCreationTime(LocalDateTime.now());
                //如果前端没有传入
                if (item.getCheckOperId() == null) {
                    item.setCheckOperId(item.getDoctorId());
                }
                if (item.getResultOperId() == null) {
                    item.setResultOperId(item.getDoctorId());
                }
            }
        }
        return saveOrUpdateBatch(items);
    }

    @Override
    @Transactional
    public boolean updateState(List<Integer> ids, Integer state) {
        if(ids == null || ids.isEmpty()){
            return false;
        }
        QueryWrapper<CheckApply> wrapper = new QueryWrapper<>();
        wrapper.in("ID", ids);
        
        CheckApply checkApply = new CheckApply();
        checkApply.setState(state);
        
        return update(checkApply, wrapper);
    }
} 