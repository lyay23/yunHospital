package com.neuedu.hisweb.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.hisweb.entity.Department;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neuedu.hisweb.entity.vo.ConstantItemVo;
import com.neuedu.hisweb.entity.vo.DepartmentVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lynn
 * @since 2023-07-29
 */
@Mapper
public interface DepartmentMapper extends BaseMapper<Department> {

    @Select("""
            <script>
                select d.id,deptcode,DeptName,DeptCategoryID,DeptCategoryID,
                    ci.ConstantName as DeptCategoryName,DeptType,cid.ConstantName DeptTypeName
                    from department d
                    inner join constantitem ci on d.DeptCategoryID=ci.id 
                    inner join constantitem cid on d.DeptType=cid.id
                    <where>
                        and d.DelMark=1
                        <if test='dtype != null and dtype!=""' >
                            and `DeptType`=#{dtype}
                        </if>
                        <if test='ctype != null and ctype!=""' >
                            and `DeptCategoryID`=#{ctype}
                        </if>
                        <if test='keyword != null and keyword!=""'>
                            and `deptcode` like CONCAT(CONCAT('%', #{keyword,jdbcType=VARCHAR}), '%')
                            or `DeptName` like CONCAT(CONCAT('%', #{keyword,jdbcType=VARCHAR}), '%')
                        </if>
                    </where>
            </script>
            """)
    Page<DepartmentVo> selectPage(Page<DepartmentVo> page, String keyword, String dtype,String ctype);

}
