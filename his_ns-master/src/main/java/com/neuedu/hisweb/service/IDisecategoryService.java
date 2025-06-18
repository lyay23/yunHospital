package com.neuedu.hisweb.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.hisweb.entity.Disecategory;
import com.baomidou.mybatisplus.extension.service.IService;
import com.neuedu.hisweb.entity.vo.DisecategoryVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lynn
 * @since 2023-07-31
 */
public interface IDisecategoryService extends IService<Disecategory> {
    Page<DisecategoryVo> selectPage(Page<DisecategoryVo> page, String keyword, String ctype);
}
