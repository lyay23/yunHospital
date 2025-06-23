package com.neuedu.hisweb.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.neuedu.hisweb.entity.Disease;
import com.neuedu.hisweb.entity.vo.DiseaseVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author litu
 * @since 2023-03-10
 */
public interface IDiseaseService extends IService<Disease> {

    IPage<DiseaseVo> selectPage(Page<DiseaseVo> page, String keyword, Integer ctype, Integer dicaType);

}
