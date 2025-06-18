package com.neuedu.hisweb.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.hisweb.entity.Disecategory;
import com.neuedu.hisweb.entity.vo.DisecategoryVo;
import com.neuedu.hisweb.mapper.DisecategoryMapper;
import com.neuedu.hisweb.service.IDisecategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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
    public Page<DisecategoryVo> selectPage(Page<DisecategoryVo> page, String keyword, String ctype) {
        return getBaseMapper().selectPage(page,keyword,ctype);
    }
}
