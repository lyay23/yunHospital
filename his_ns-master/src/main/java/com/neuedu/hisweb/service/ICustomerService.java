package com.neuedu.hisweb.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.hisweb.entity.Customer;
import com.baomidou.mybatisplus.extension.service.IService;
import com.neuedu.hisweb.entity.vo.CustomerVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lynn
 * @since 2023-08-09
 */
public interface ICustomerService extends IService<Customer> {
    Page<CustomerVo> selectPage(Page<CustomerVo> page, String keyword);

    Customer selectByIdNumber(String idnumber);
}
