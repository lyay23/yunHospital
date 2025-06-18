package com.neuedu.hisweb.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.hisweb.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.neuedu.hisweb.entity.vo.UserVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lynn
 * @since 2023-07-17
 */
public interface IUserService extends IService<User> {
    Page<UserVo> selectPage(Page<UserVo> page, String keyword, String userType, String dept, String docType);

    boolean updatePwd(Integer uid,String oldPwd,String newPwd);
}
