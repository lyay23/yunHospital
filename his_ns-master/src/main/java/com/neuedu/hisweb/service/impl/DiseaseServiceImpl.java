package com.neuedu.hisweb.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neuedu.hisweb.entity.Disease;
import com.neuedu.hisweb.entity.vo.DiseaseVo;
import com.neuedu.hisweb.mapper.DiseaseMapper;
import com.neuedu.hisweb.service.IDiseaseService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author litu
 * @since 2023-03-10
 */
@Service
public class DiseaseServiceImpl extends ServiceImpl<DiseaseMapper, Disease> implements IDiseaseService {

    @Override
    public IPage<DiseaseVo> selectPage(Page<DiseaseVo> page, String keyword, Integer ctype, Integer dicaType) {
        return baseMapper.selectPage(page, keyword, ctype, dicaType);
    }
}
