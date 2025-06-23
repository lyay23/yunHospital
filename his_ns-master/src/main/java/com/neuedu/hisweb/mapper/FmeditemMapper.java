package com.neuedu.hisweb.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.hisweb.entity.Fmeditem;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neuedu.hisweb.entity.vo.FmeditemVo;
import com.neuedu.hisweb.entity.vo.UserVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;
import java.util.List;

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
                   LEFT JOIN constantitem ci on f.RecordType=ci.ID
                    <where>
                        and f.DelMark=1
                        <if test='expClassIds != null and expClassIds.size() > 0' >
                            and `ExpClassID` in
                            <foreach collection="expClassIds" item="item" open="(" separator="," close=")">
                                #{item}
                            </foreach>
                        </if>
                        <if test='dept != null and dept!=""' >
                            and deptid=#{dept}
                        </if>
                        <if test='keyword != null and keyword!=""'>
                            and (`ItemCode` like CONCAT(CONCAT('%', #{keyword,jdbcType=VARCHAR}), '%')
                            or `ItemName` like CONCAT(CONCAT('%', #{keyword,jdbcType=VARCHAR}), '%')
                            or `MnemonicCode` like CONCAT(CONCAT('%', #{keyword,jdbcType=VARCHAR}), '%'))
                        </if>
                    </where>
                    order by CreationDate desc
            </script>
            """)
    Page<FmeditemVo> selectPage(Page<FmeditemVo> page, @Param("keyword") String keyword, @Param("expClassIds") List<String> expClassIds, @Param("dept") String dept);

}
