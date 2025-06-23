package com.neuedu.hisweb.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neuedu.hisweb.entity.CheckRelation;
import com.neuedu.hisweb.entity.vo.FmeditemVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CheckRelationMapper extends BaseMapper<CheckRelation> {

    @Select("""
            SELECT fm.ID as id, fm.ItemName as itemName, fm.Price as price, d.DeptName as deptName, fm.DeptID as deptId
            FROM checkrelation cr
            JOIN fmeditem fm ON cr.CheckProjID = fm.ID
            JOIN department d ON fm.DeptId = d.ID
            WHERE cr.CheckTempID = #{templateId}
            """)
    List<FmeditemVo> findItemsByTemplateId(Integer templateId);
} 