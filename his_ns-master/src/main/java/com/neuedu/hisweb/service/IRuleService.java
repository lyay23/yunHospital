package com.neuedu.hisweb.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.hisweb.entity.Rule;
import com.baomidou.mybatisplus.extension.service.IService;
import com.neuedu.hisweb.entity.vo.RuleVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lynn
 * @since 2023-07-31
 */
public interface IRuleService extends IService<Rule> {
    Page<RuleVo> selectPage(Page<RuleVo> page, String keyword,String deptId);
}
