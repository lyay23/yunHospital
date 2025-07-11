package com.neuedu.hisweb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.hisweb.entity.Customer;
import com.neuedu.hisweb.entity.vo.CustomerVo;
import com.neuedu.hisweb.mapper.CustomerMapper;
import com.neuedu.hisweb.service.ICustomerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lynn
 * @since 2023-08-09
 */
@Service
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer> implements ICustomerService {

    @Override
    public Page<CustomerVo> selectPage(Page<CustomerVo> page, String keyword) {
        return getBaseMapper().selectPage(page,keyword);
    }

    @Override
    public Customer selectByIdNumber(String idnumber) {
        LambdaQueryWrapper<Customer> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Customer::getIdnumber, idnumber);
        return getBaseMapper().selectOne(queryWrapper);
    }
}
