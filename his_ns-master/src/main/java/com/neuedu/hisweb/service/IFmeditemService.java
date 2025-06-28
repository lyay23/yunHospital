package com.neuedu.hisweb.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.hisweb.entity.Fmeditem;
import com.baomidou.mybatisplus.extension.service.IService;
import com.neuedu.hisweb.entity.vo.FmeditemVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lynn
 * @since 2023-07-31
 */
public interface IFmeditemService extends IService<Fmeditem> {
    Page<FmeditemVo> selectPage(Page<FmeditemVo> page, String keyword, List<String> expClassIds, String dept);

    Integer importFmeditem(MultipartFile file);
}
