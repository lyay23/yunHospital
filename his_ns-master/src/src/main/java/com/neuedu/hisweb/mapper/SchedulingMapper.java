package com.neuedu.hisweb.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.hisweb.entity.Scheduling;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neuedu.hisweb.entity.vo.SchedulingVo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

/**
 * <p>
 *  Mapper 接口
 * </p>
 * @author lynn
 * @since 2023-07-31
 */
@Mapper
public interface SchedulingMapper extends BaseMapper<Scheduling> {
    @Select("""
            <script>
               select s.id,schedDate,s.DeptId,deptName,s.UserId,realname,
                      rl.id registlevelId,rl.registName,rl.registFee,
                      rl.RegistQuota,noon,s.regNum,
                      ruleID,ruleName,week,num
               from scheduling s
                   inner join department d on s.DeptID=d.ID
                   inner join user u on s.UserID=u.ID
                   inner join registlevel  rl on rl.id=u.RegistLeID
                   inner join rule r on s.RuleID=r.ID
                    <where>
                        and s.delMark=1
                        <if test='deptId != null and deptId!=""'>
                            and s.deptId=#{deptId}
                        </if>
                        <if test='userId != null and userId!=""'>
                            and s.userId=#{userId}
                        </if>
                        <if test='regLevel != null and regLevel!=""'>
                           and rl.id=#{regLevel}
                        </if>
                         <if test='noon != null and noon!=""'>
                           and noon=#{noon}
                        </if>
                         <if test='keyword != null and keyword!=""'>
                            and (`ruleName` like CONCAT(CONCAT('%', #{keyword,jdbcType=VARCHAR}), '%')
                            or `realname` like CONCAT(CONCAT('%', #{keyword,jdbcType=VARCHAR}), '%'))
                        </if>
                        
                        <if test='start != null and start!="" and end != null and end!=""'  >
                            and `schedDate` between date(#{start}) and date(#{end})
                        </if>
                    </where>
                    order by schedDate desc
            </script>
            """)
    Page<SchedulingVo> selectPage(Page<SchedulingVo> page, String keyword, String deptId,String userId,String regLevel,String noon,String start, String end);


    @Update("update scheduling set regNum=regNum+1 where id=#{id}")
    int updateByRegister(Integer id);

    @Update("update scheduling set regNum=regNum-1 where schedDate=date(#{visitDate}) and noon=#{noon} and userId=#{doctorId} ")
    int updateByBackRegister(String visitDate,String noon,Integer doctorId);

}
