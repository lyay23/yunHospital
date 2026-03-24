package com.neuedu.hisweb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.hisweb.entity.Drugs;
import com.neuedu.hisweb.mapper.DrugsMapper;
import com.neuedu.hisweb.service.IDrugsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Collection;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lynn
 * @since 2024-07-27
 */
@Service
public class DrugsServiceImpl extends ServiceImpl<DrugsMapper, Drugs> implements IDrugsService {

    @Override
    @Cacheable(cacheNames = "drugs:page",
            key = "'pn=' + #page.current + ':count=' + #page.size + ':keyword=' + #keyword")
    public Page<Drugs> selectPage(Page<Drugs> page, String keyword) {
        QueryWrapper<Drugs> wrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(keyword)) {
            wrapper.nested(i -> i.like("DrugsCode", keyword).or()
                                 .like("DrugsName", keyword).or()
                                 .like("MnemonicCode", keyword));
        }
        wrapper.eq("Delmark", 1);
        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    @CacheEvict(cacheNames = "drugs:page", allEntries = true)
    public boolean save(Drugs entity) {
        return super.save(entity);
    }

    @Override
    @CacheEvict(cacheNames = "drugs:page", allEntries = true)
    public boolean saveBatch(Collection<Drugs> entityList) {
        return super.saveBatch(entityList);
    }

    @Override
    @CacheEvict(cacheNames = "drugs:page", allEntries = true)
    public boolean updateById(Drugs entity) {
        return super.updateById(entity);
    }

    @Override
    @CacheEvict(cacheNames = "drugs:page", allEntries = true)
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }
} 
