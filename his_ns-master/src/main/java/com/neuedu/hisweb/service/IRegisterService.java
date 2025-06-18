package com.neuedu.hisweb.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.hisweb.entity.Register;
import com.baomidou.mybatisplus.extension.service.IService;
import com.neuedu.hisweb.entity.vo.RegParam;
import com.neuedu.hisweb.entity.vo.RegisterVo;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lynn
 * @since 2023-08-09
 */
@Transactional
public interface IRegisterService extends IService<Register> {
    boolean saveRegister(RegParam param);
    Page<RegisterVo> selectPage(Page<RegisterVo> page,Integer deptId,Integer docId,Integer state,String keyword,String regDate);
    boolean updateRegisterState(RegParam param);
}
