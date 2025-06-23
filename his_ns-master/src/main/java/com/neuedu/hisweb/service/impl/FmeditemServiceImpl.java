package com.neuedu.hisweb.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.hisweb.entity.Fmeditem;
import com.neuedu.hisweb.entity.vo.FmeditemVo;
import com.neuedu.hisweb.mapper.FmeditemMapper;
import com.neuedu.hisweb.service.IFmeditemService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
public class FmeditemServiceImpl extends ServiceImpl<FmeditemMapper, Fmeditem> implements IFmeditemService {

    @Override
    public Page<FmeditemVo> selectPage(Page<FmeditemVo> page, String keyword, List<String> expClassIds, String dept) {
        return getBaseMapper().selectPage(page,keyword,expClassIds,dept);
    }
}
