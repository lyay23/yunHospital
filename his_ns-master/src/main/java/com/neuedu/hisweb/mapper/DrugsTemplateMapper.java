package com.neuedu.hisweb.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.hisweb.entity.DrugsTemplate;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neuedu.hisweb.entity.vo.DrugsTemplateVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lynn
 * @since 2024-07-27
 */
public interface DrugsTemplateMapper extends BaseMapper<DrugsTemplate> {

    @Select("SELECT d.DrugsName, d.DrugsFormat, d.DrugsUnit, dd.Dosage, dd.Frequency, dd.DrugsID " +
            "FROM drugsdetailed dd " +
            "JOIN drugs d ON dd.DrugsID = d.ID " +
            "WHERE dd.DrugsTempID = #{templateId}")
    List<DrugsTemplateVo> selectDetail(@Param("templateId") String templateId);

} 