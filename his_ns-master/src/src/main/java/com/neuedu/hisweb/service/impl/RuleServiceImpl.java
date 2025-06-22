package com.neuedu.hisweb.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.hisweb.entity.Rule;
import com.neuedu.hisweb.entity.vo.RuleVo;
import com.neuedu.hisweb.mapper.RuleMapper;
import com.neuedu.hisweb.service.IRuleService;
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
public class RuleServiceImpl extends ServiceImpl<RuleMapper, Rule> implements IRuleService {

    @Override
    public Page<RuleVo> selectPage(Page<RuleVo> page, String keyword,String deptId) {
        return getBaseMapper().selectPage(page,keyword,deptId);
    }
}
