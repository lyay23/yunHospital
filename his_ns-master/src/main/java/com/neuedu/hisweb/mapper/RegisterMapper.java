package com.neuedu.hisweb.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.hisweb.entity.Register;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neuedu.hisweb.entity.vo.RegisterVo;
import com.neuedu.hisweb.entity.vo.SchedulingVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lynn
 * @since 2023-08-09
 */
@Mapper
public interface RegisterMapper extends BaseMapper<Register> {
    @Select("""
            <script>
              select r.id,r.CaseNumber,m.RealName,m.Gender,m.idnumber,m.phone,m.birthdate,
                   r.age,r.AgeType,m.addr,r.VisitDate,Noon,r.DeptID,d.DeptName,r.UserID, 
                   doc.RealName DoctorName,r.RegistLeID,rl.RegistName,RegistFee,r.SettleID,r.IsBook,
                   r.RegistTime,r.RegisterID,u.RealName RegisterName,r.VisitState,r.MedicalCardId,
                   r.TimeInterval,r.Channel,ConstantName channelName
                   from register r
                        inner join department d on r.DeptID=d.ID
                        inner join registlevel rl on r.RegistLeID=rl.ID
                        inner join medicalcard m on r.MedicalCardId=m.id
                        inner join user doc on r.UserID=doc.ID
                        inner join user u on r.RegisterID=u.ID
                        inner join constantitem c on r.Channel=c.ID
                    <where>
                        <if test='deptId != null and deptId!=""'>
                            and r.deptId=#{deptId}
                        </if>
                        <if test='docId != null and docId!=""'>
                            and r.UserID=#{docId}
                        </if>
                        <if test='state != null and state!=""'>
                            and r.VisitState=#{state}
                        </if>
                        <if test='regDate != null'  >
                            and date(r.visitDate)=#{regDate,jdbcType=VARCHAR}
                        </if>
                         <if test='keyword != null and keyword!=""'>
                            and (m.idnumber` like CONCAT(CONCAT('%', #{keyword,jdbcType=VARCHAR}), '%')
                            or m.RealName like CONCAT(CONCAT('%', #{keyword,jdbcType=VARCHAR}), '%')
                            or m.phone like CONCAT(CONCAT('%', #{keyword,jdbcType=VARCHAR}), '%')
                            or r.CaseNumber like CONCAT(CONCAT('%', #{keyword,jdbcType=VARCHAR}), '%'))
                        </if>
                        
                    </where>
                    order by r.id desc
            </script>
            """)
    Page<RegisterVo> selectPage(Page<RegisterVo> page,Integer deptId,Integer docId,Integer state,String keyword,String regDate);


    @Select("""
            <script>
               select CaseNumber from Register order by id desc limit 0,1
            </script>
            """)
    public Integer getMaxCaseNumber();

    @Update("update register set VisitState=#{state} where id=#{id}")
    public int updateVisitState(Integer id,Integer state);
}
