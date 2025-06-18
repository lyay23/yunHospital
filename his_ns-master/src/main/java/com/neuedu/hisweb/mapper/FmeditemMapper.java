package com.neuedu.hisweb.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.hisweb.entity.Fmeditem;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neuedu.hisweb.entity.vo.FmeditemVo;
import com.neuedu.hisweb.entity.vo.UserVo;
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
public interface FmeditemMapper extends BaseMapper<Fmeditem> {
    @Select("""
            <script>
                select f.id,ItemCode,ItemName,Format,Price,ExpClassID,expName,DeptId,DeptName,MnemonicCode,
                   RecordType,constantName RecordTypeName,CreationDate,LastUpdateDate
                   from fmeditem f
                   INNER JOIN expenseclass e on f.ExpClassID=e.ID
                   INNER JOIN department d on f.DeptId=d.ID
                   INNER JOIN constantitem ci on f.RecordType=ci.ID
                    <where>
                        and f.DelMark=1
                        <if test='expClassId != null and expClassId!=""' >
                            and `ExpClassID`=#{expClassId}
                        </if>
                        <if test='dept != null and dept!=""' >
                            and deptid=#{dept}
                        </if>
                        <if test='ctype != null and ctype!=""' >
                            and RecordType=#{ctype}
                        </if>
                        <if test='keyword != null and keyword!=""'>
                            and `ItemCode` like CONCAT(CONCAT('%', #{keyword,jdbcType=VARCHAR}), '%')
                            or `ItemName` like CONCAT(CONCAT('%', #{keyword,jdbcType=VARCHAR}), '%')
                        </if>
                    </where>
            </script>
            """)
    Page<FmeditemVo> selectPage(Page<FmeditemVo> page, String keyword, String expClassId, String dept, String ctype);

}
