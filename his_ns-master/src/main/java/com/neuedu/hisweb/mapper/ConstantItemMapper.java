package com.neuedu.hisweb.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.hisweb.entity.ConstantItem;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neuedu.hisweb.entity.vo.ConstantItemVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lynn
 * @since 2023-07-26
 */
@Mapper
public interface ConstantItemMapper extends BaseMapper<ConstantItem> {

    @Select("""
            <script>
                select ci.id,ConstantTypeID,ConstantTypeName,ConstantCode,ConstantName 
                from constantitem ci 
                    inner join constanttype ct on ci.ConstantTypeID=ct.ID
                    <where>
                        and ci.delMark=1
                        <if test='ctype != null and ctype!=""' >
                            and `ConstantTypeID`=#{ctype}
                        </if>
                        <if test='keyword != null and keyword!=""'>
                            and `ConstantCode` like CONCAT(CONCAT('%', #{keyword,jdbcType=VARCHAR}), '%')
                            or `ConstantName` like CONCAT(CONCAT('%', #{keyword,jdbcType=VARCHAR}), '%')
                        </if>
                    </where>
            </script>
            """)
    Page<ConstantItemVo> selectPage(Page<ConstantItemVo> page, String keyword,String ctype);


}
