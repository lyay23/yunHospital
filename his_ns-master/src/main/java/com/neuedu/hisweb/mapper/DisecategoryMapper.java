package com.neuedu.hisweb.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.hisweb.entity.Disecategory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neuedu.hisweb.entity.vo.DiseaseVo;
import com.neuedu.hisweb.entity.vo.DisecategoryVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lynn
 * @since 2023-07-31
 */
@Mapper
public interface DisecategoryMapper extends BaseMapper<Disecategory> {
    @Select("""
            <script>
               select d.id,dicaCode,dicaName,sequenceNo,dicaType,ConstantName dicaTypeName
               from DiseCategory d
               inner join constantitem c on d.DicaType=c.ID
                    <where>
                        and d.delMark=1
                        <if test='ctype != null and ctype!=""' >
                            and `dicaType`=#{ctype}
                        </if>
                        <if test='keyword != null and keyword!=""'>
                            and `dicaCode` like CONCAT(CONCAT('%', #{keyword,jdbcType=VARCHAR}), '%')
                            or `dicaName` like CONCAT(CONCAT('%', #{keyword,jdbcType=VARCHAR}), '%')
                        </if>
                    </where>
                    ORDER BY sequenceNo asc
            </script>
            """)
    Page<DisecategoryVo> selectPage(Page<DisecategoryVo> page, String keyword, String ctype);
}
