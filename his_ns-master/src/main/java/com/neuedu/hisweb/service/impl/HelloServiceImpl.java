package com.neuedu.hisweb.service.impl;

import com.neuedu.hisweb.entity.Hello;
import com.neuedu.hisweb.mapper.HelloMapper;
import com.neuedu.hisweb.service.HelloService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xj
 * @since 2025-06-11
 */
@Service
public class HelloServiceImpl extends ServiceImpl<HelloMapper, Hello> implements HelloService {

}
