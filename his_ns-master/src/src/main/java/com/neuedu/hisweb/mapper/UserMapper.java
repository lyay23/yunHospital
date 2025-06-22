package com.neuedu.hisweb.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.hisweb.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neuedu.hisweb.entity.vo.DepartmentVo;
import com.neuedu.hisweb.entity.vo.UserVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;


/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lynn
 * @since 2023-07-17
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("""
            <script>
                select u.id,username,realname,usetype,ci1.ConstantName usetypeName,
                	doctitleId,ci.ConstantName AS docTitle,
                	isscheduling,deptid,d.DeptName dept,
                	registLeID,r.RegistName registLe
                from
                	USER u
                	INNER JOIN constantitem ci1 ON u.usetype = ci1.ID
                    INNER JOIN constantitem ci ON u.DocTitleID = ci.ID
                    INNER JOIN department d ON u.DeptID = d.ID
                    INNER JOIN registlevel r ON u.RegistLeID = r.ID
                    <where>
                        and u.DelMark=1
                        <if test='userType != null and userType!=""' >
                            and `usetype`=#{userType}
                        </if>
                        <if test='dept != null and dept!=""' >
                            and u.deptid=#{dept}
                        </if>
                        <if test='docType != null and docType!=""' >
                            and u.doctitleId=#{docType}
                        </if>
                        <if test='keyword != null and keyword!=""'>
                            and `username` like CONCAT(CONCAT('%', #{keyword,jdbcType=VARCHAR}), '%')
                            or `realname` like CONCAT(CONCAT('%', #{keyword,jdbcType=VARCHAR}), '%')
                        </if>
                    </where>
            </script>
            """)
    Page<UserVo> selectPage(Page<UserVo> page, String keyword, String userType, String dept, String docType);

    @Update("update user set password=#{newPwd,jdbcType=VARCHAR} where id=#{id} and  password=#{oldPwd,jdbcType=VARCHAR}")
    boolean updatePwd(Integer uid,String oldPwd,String newPwd);

}
