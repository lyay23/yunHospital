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
            SELECT
                ca.*,
                r.CaseNumber as caseNumber,
                mc.RealName as patientName,
                r.age as age,
                d.DeptName as deptName,
                fm.Price as price,
                fm.ItemName as itemName,
                doc.RealName as doctorName,
                sc.SettleName as settleCategoryName,
                mr.result_desc as resultDesc,
                mr.result_images as resultImages
            FROM
                checkapply ca
            LEFT JOIN
                fmeditem fm ON ca.ItemID = fm.ID
            LEFT JOIN
                register r ON ca.RegistID = r.ID
            LEFT JOIN
                medicalcard mc ON r.MedicalCardId = mc.id
            LEFT JOIN
                department d ON r.DeptID = d.ID
            LEFT JOIN
                user doc ON ca.DoctorID = doc.ID
            LEFT JOIN
                settlecategory sc ON r.SettleID = sc.ID
            LEFT JOIN
                medical_result mr ON ca.ID = mr.item_id AND mr.item_type = ca.RecordType
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