package com.neuedu.hisweb.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.hisweb.entity.MedicalCard;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neuedu.hisweb.entity.vo.MedicalCardVo;
import com.neuedu.hisweb.entity.vo.SchedulingVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lynn
 * @since 2023-08-09
 */
@Mapper
public interface MedicalCardMapper extends BaseMapper<MedicalCard> {

    @Select("""
            <script>
              select mc.id,mc.realname,mc.gender,mc.idnumber,mc.birthdate,mc.phone,mc.addr,
                     cardtype,ct.ConstantName cardtypeName,cardNo,
                     customerId,c.realName customerName,
                     relationship,cr.ConstantName relationshipName,
                     mc.createdate,mc.channel,cc.ConstantName channelName
                     from MedicalCard mc
                     inner join constantitem ct on mc.cardtype=ct.id
                     left join customer c on mc.customerId=c.id
                     left join constantitem cr on mc.relationship=cr.ID
                     left join constantitem cc on mc.channel=cc.ID
                    <where>
                        and mc.delMark=1
                         <if test='keyword != null and keyword!=""'>
                            and (mc.realname like CONCAT(CONCAT('%', #{keyword,jdbcType=VARCHAR}), '%')
                                or c.realName like CONCAT(CONCAT('%', #{keyword,jdbcType=VARCHAR}), '%')
                                or mc.idnumber like CONCAT(CONCAT('%', #{keyword,jdbcType=VARCHAR}), '%')
                                or cardNo like CONCAT(CONCAT('%', #{keyword,jdbcType=VARCHAR}), '%')
                                or mc.phone like CONCAT(CONCAT('%', #{keyword,jdbcType=VARCHAR}), '%'))
                        </if>
                    </where>
                    order by mc.createdate desc
            </script>
            """)
    Page<MedicalCardVo> selectPage(Page<MedicalCardVo> page, String keyword);


    @Select("""
            <script>
              select cardNo  from MedicalCard where cardtype=185  order by id desc
              LIMIT 0,1
            </script>
            """)
    Integer getMaxCardNo();

}
