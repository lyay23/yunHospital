package com.neuedu.hisweb.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.hisweb.entity.Disease;
import com.neuedu.hisweb.entity.vo.DiseaseVo;
import com.neuedu.hisweb.mapper.DiseaseMapper;
import com.neuedu.hisweb.service.IDiseaseService;
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
public class DiseaseServiceImpl extends ServiceImpl<DiseaseMapper, Disease> implements IDiseaseService {

    @Override
    public Page<DiseaseVo> selectPage(Page<DiseaseVo> page, String keyword, String ctype) {
        return getBaseMapper().selectPage(page,keyword,ctype);
    }
}
