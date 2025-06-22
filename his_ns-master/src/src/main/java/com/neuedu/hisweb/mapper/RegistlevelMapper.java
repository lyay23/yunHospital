package com.neuedu.hisweb.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.hisweb.entity.Registlevel;
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
 * @since 2023-07-30
 */
@Mapper
public interface RegistlevelMapper extends BaseMapper<Registlevel> {

    @Select("""
            <script>
               select id,registCode,registName,sequenceNo,registFee,RegistQuota
               from registlevel
                    <where>
                        and delMark=1
                        <if test='keyword != null and keyword!=""'>
                            and `registCode` like CONCAT(CONCAT('%', #{keyword,jdbcType=VARCHAR}), '%')
                            or `registName` like CONCAT(CONCAT('%', #{keyword,jdbcType=VARCHAR}), '%')
                        </if>
                    </where>
                     ORDER BY sequenceNo asc
            </script>
            """)
    Page<Registlevel> selectPage(Page<Registlevel> page, String keyword);

}
