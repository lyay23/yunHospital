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
            select
                ca.*,
                d.DeptName as deptName,
                fm.Price,
                fm.ItemName as itemName,
                u.RealName as doctorName,
                sc.SettleName as settleCategoryName,
                mr.result_desc as resultDesc,
                mr.result_images as resultImages
            from checkapply ca
            left join fmeditem fm on ca.ItemID=fm.ID
            left join department d on fm.DeptID=d.ID
            left join user u on ca.DoctorID = u.ID
            left join register r on ca.RegistID = r.ID
            left join settlecategory sc on r.SettleID = sc.ID
            left join medical_result mr on ca.ID = mr.item_id and mr.item_type = ca.RecordType
            <where>
                <if test="ca.registId != null" >
                    and ca.RegistID = #{ca.registId}
                </if>
                <if test="ca.recordType != null" >
                    and ca.RecordType = #{ca.recordType}
                </if>
            </where>
            order by ca.CreationTime desc
            </script>
            """)
    Page<CheckApplyVo> selectPage(Page<CheckApplyVo> page, @Param("ca") CheckApplyVo checkApply);
}