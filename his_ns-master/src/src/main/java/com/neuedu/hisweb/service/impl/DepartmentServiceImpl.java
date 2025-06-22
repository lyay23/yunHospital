package com.neuedu.hisweb.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.hisweb.entity.Department;
import com.neuedu.hisweb.entity.vo.ConstantItemVo;
import com.neuedu.hisweb.entity.vo.DepartmentVo;
import com.neuedu.hisweb.mapper.DepartmentMapper;
import com.neuedu.hisweb.service.IDepartmentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lynn
 * @since 2023-07-29
 */
@Service
public class DepartmentServiceImpl extends ServiceImpl<DepartmentMapper, Department> implements IDepartmentService {

    @Override
    public Page<DepartmentVo> selectPage(Page<DepartmentVo> page, String keyword, String dtype, String ctype) {
        return getBaseMapper().selectPage(page,keyword,dtype,ctype);
    }
}
