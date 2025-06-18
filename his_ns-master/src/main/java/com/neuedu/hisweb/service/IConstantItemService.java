package com.neuedu.hisweb.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.hisweb.entity.ConstantItem;
import com.baomidou.mybatisplus.extension.service.IService;
import com.neuedu.hisweb.entity.vo.ConstantItemVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lynn
 * @since 2023-07-26
 */
public interface IConstantItemService extends IService<ConstantItem> {
    Page<ConstantItemVo> selectPage(Page<ConstantItemVo> page, String keyword,String ctype);
}
