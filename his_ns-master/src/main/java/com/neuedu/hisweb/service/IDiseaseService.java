package com.neuedu.hisweb.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.hisweb.entity.Disease;
import com.baomidou.mybatisplus.extension.service.IService;
import com.neuedu.hisweb.entity.vo.DiseaseVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lynn
 * @since 2023-07-31
 */
public interface IDiseaseService extends IService<Disease> {
    Page<DiseaseVo> selectPage(Page<DiseaseVo> page, String keyword, String ctype);
}
