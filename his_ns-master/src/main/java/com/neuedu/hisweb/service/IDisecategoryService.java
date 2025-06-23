package com.neuedu.hisweb.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.hisweb.entity.Disecategory;
import com.baomidou.mybatisplus.extension.service.IService;
import com.neuedu.hisweb.entity.vo.DisecategoryVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lynn
 * @since 2023-07-30
 */
public interface IDisecategoryService extends IService<Disecategory> {

    Page<DisecategoryVo> selectPage(Integer pn, Integer count, String keyword, String dicaName, Integer dicaType);

    List<DisecategoryVo> selectAll(String keyword);
}
