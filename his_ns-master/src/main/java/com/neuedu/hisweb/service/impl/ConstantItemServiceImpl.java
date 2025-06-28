package com.neuedu.hisweb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.hisweb.entity.ConstantItem;
import com.neuedu.hisweb.entity.vo.ConstantItemVo;
import com.neuedu.hisweb.mapper.ConstantItemMapper;
import com.neuedu.hisweb.service.IConstantItemService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lynn
 * @since 2023-07-26
 */
@Service
public class ConstantItemServiceImpl extends ServiceImpl<ConstantItemMapper, ConstantItem> implements IConstantItemService {

    @Override
    public Page<ConstantItemVo> selectPage(Page<ConstantItemVo> page, String keyword,String ctype) {
        return getBaseMapper().selectPage(page,keyword,ctype);
    }

    @Override
    public List<ConstantItemVo> selectByType(String type) {
        return getBaseMapper().selectByType(type);
    }
}
