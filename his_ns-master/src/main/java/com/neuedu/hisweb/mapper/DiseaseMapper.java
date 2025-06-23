package com.neuedu.hisweb.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.hisweb.entity.Disease;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neuedu.hisweb.entity.vo.ConstantItemVo;
import com.neuedu.hisweb.entity.vo.DiseaseVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lynn
 * @since 2023-07-31
 */
@Mapper
public interface DiseaseMapper extends BaseMapper<Disease> {

    @Select("""
            <script>
                select d.id,diseaseCode,diseaseName,diseaseICD,diseCategoryID,dicaName as diseCategoryName
                from disease d inner join diseCategory dc on d.diseCategoryID=dc.id
                    <where>
                        and d.delMark=1
                        <if test='ctype != null and ctype!=""' >
                            and `diseCategoryID`=#{ctype}
                        </if>
                        <if test='keyword != null and keyword!=""'>
                            and `diseaseCode` like CONCAT(CONCAT('%', #{keyword,jdbcType=VARCHAR}), '%')
                            or `diseaseName` like CONCAT(CONCAT('%', #{keyword,jdbcType=VARCHAR}), '%')
                        </if>
                    </where>
            </script>
            """)
    Page<DiseaseVo> selectPage(Page<DiseaseVo> page, @Param("keyword") String keyword, @Param("ctype") Integer ctype, @Param("dicaType") Integer dicaType);

    IPage<DiseaseVo> selectPage(Page<DiseaseVo> page, @Param("ew") QueryWrapper<DiseaseVo> wrapper);

}
