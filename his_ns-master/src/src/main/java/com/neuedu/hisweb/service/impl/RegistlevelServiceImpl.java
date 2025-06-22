package com.neuedu.hisweb.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.hisweb.entity.Registlevel;
import com.neuedu.hisweb.mapper.RegistlevelMapper;
import com.neuedu.hisweb.service.IRegistlevelService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lynn
 * @since 2023-07-30
 */
@Service
public class RegistlevelServiceImpl extends ServiceImpl<RegistlevelMapper, Registlevel> implements IRegistlevelService {

    @Override
    public Page<Registlevel> selectPage(Page<Registlevel> page, String keyword) {
        return getBaseMapper().selectPage(page,keyword);
    }
}
