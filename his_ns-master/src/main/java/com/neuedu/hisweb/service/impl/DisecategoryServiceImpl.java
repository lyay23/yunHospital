package com.neuedu.hisweb.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.neuedu.hisweb.entity.Disecategory;
import com.neuedu.hisweb.entity.vo.DisecategoryVo;
import com.neuedu.hisweb.mapper.DisecategoryMapper;
import com.neuedu.hisweb.service.IDisecategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lynn
 * @since 2023-07-31
 */
@Service
public class DisecategoryServiceImpl extends ServiceImpl<DisecategoryMapper, Disecategory> implements IDisecategoryService {

    @Override
    public Page<DisecategoryVo> selectPage(Integer pn, Integer count, String keyword, String dicaName, Integer dicaType) {
        Page<DisecategoryVo> page = new Page<>(pn, count);
        return getBaseMapper().selectPage(page, keyword, dicaName, dicaType);
    }

    @Override
    public List<DisecategoryVo> selectAll(String keyword) {
        return getBaseMapper().selectAll(keyword);
    }
}
