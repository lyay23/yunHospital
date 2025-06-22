package com.neuedu.hisweb.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.hisweb.entity.Registlevel;
import com.neuedu.hisweb.entity.Rule;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neuedu.hisweb.entity.vo.RuleVo;
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
public interface RuleMapper extends BaseMapper<Rule> {
    @Select("""
            <script>
               select r.id,ruleName,r.deptId,deptName,
                    userId,realname,week,num
                    from rule r
                    inner join department d on r.DeptID=d.ID
                    inner join user u on r.UserID=u.ID
                    <where>
                        and r.delMark=1
                        <if test='deptId != null and deptId!=""'>
                            and r.`deptId`=#{deptId}
                        </if>
                        <if test='keyword != null and keyword!=""'>
                            and `ruleName` like CONCAT(CONCAT('%', #{keyword,jdbcType=VARCHAR}), '%')
                            or  `realname` like CONCAT(CONCAT('%', #{keyword,jdbcType=VARCHAR}), '%')
                        </if>
                    </where>
            </script>
            """)
    Page<RuleVo> selectPage(Page<RuleVo> page, String keyword,String deptId);


}
