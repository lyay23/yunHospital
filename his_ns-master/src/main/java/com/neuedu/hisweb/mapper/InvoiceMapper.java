package com.neuedu.hisweb.mapper;

import com.neuedu.hisweb.entity.Invoice;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lynn
 * @since 2023-08-09
 */
public interface InvoiceMapper extends BaseMapper<Invoice> {

    @Select("select * from invoice where registId = #{registId} limit 1")
    Invoice selectByRegistId(@Param("registId") Integer registId);
}
