package com.neuedu.hisweb.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.hisweb.entity.User;
import com.neuedu.hisweb.entity.vo.UserVo;
import com.neuedu.hisweb.mapper.UserMapper;
import com.neuedu.hisweb.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lynn
 * @since 2023-07-17
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Override
    public Page<UserVo> selectPage(Page<UserVo> page, String keyword, String userType, String dept, String docType) {
        return getBaseMapper().selectPage(page,keyword,userType,dept,docType);
    }

    @Override
    public boolean updatePwd(Integer uid, String oldPwd, String newPwd) {
        return getBaseMapper().updatePwd(uid,oldPwd,newPwd);
    }
}
