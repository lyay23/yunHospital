package com.neuedu.hisweb.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.hisweb.entity.MedicalCard;
import com.baomidou.mybatisplus.extension.service.IService;
import com.neuedu.hisweb.entity.vo.MedicalCardVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lynn
 * @since 2023-08-09
 */
public interface IMedicalCardService extends IService<MedicalCard> {
    Page<MedicalCardVo> selectPage(Page<MedicalCardVo> page, String keyword);

    Integer getMaxCardNo();
}
