package com.neuedu.hisweb.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.hisweb.entity.Registlevel;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lynn
 * @since 2023-07-30
 */
public interface IRegistlevelService extends IService<Registlevel> {
    Page<Registlevel> selectPage(Page<Registlevel> page, String keyword);
}
