package com.neuedu.hisweb.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.hisweb.entity.Customer;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neuedu.hisweb.entity.vo.CustomerVo;
import com.neuedu.hisweb.entity.vo.MedicalCardVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lynn
 * @since 2023-08-09
 */
@Mapper
public interface CustomerMapper extends BaseMapper<Customer> {

    @Select("""
            <script>
              select c.id,c.realName,c.gender,c.idnumber,c.birthdate,c.phone,c.createdate,
                     c.channel, cc.ConstantName channelName ,c.delMark
              from customer c
                   left join constantitem cc on c.channel=cc.ID
                    <where>
                        and c.delMark=1
                         <if test='keyword != null and keyword!=""'>
                            and ( c.realName like CONCAT(CONCAT('%', #{keyword,jdbcType=VARCHAR}), '%')
                                  or c.idnumber like CONCAT(CONCAT('%', #{keyword,jdbcType=VARCHAR}), '%'))
                        </if>
                    </where>
                    order by c.createdate desc
            </script>
            """)
    Page<CustomerVo> selectPage(Page<CustomerVo> page, String keyword);


}
