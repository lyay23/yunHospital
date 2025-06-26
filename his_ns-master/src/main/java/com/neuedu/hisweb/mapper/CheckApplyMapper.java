package com.neuedu.hisweb.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.hisweb.entity.CheckApply;
import com.neuedu.hisweb.entity.vo.CheckApplyVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CheckApplyMapper extends BaseMapper<CheckApply> {

    @Select("""
            <script>
            select ca.*,d.DeptName as deptName,fm.Price, fm.ItemName as itemName from checkapply ca
            left join fmeditem fm on ca.ItemID=fm.ID
            left join department d on ca.Position=d.ID
            <where>
                <if test='registId != null' >
                    and ca.RegistID = #{registId}
                </if>
                <if test='recordType != null' >
                    and ca.RecordType = #{recordType}
                </if>
            </where>
            order by ca.CreationTime desc
            </script>
            """)
    Page<CheckApplyVo> selectPage(Page<CheckApplyVo> page, @Param("registId") Integer registId, @Param("recordType") Integer recordType);
} 