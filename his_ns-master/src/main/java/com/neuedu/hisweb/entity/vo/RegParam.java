package com.neuedu.hisweb.entity.vo;

import com.neuedu.hisweb.entity.Register;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author lynn
 * @since 2023-07-31
 */
@Data
public class RegParam implements Serializable {
    private static final long serialVersionUID = 1L;
    private Register register;
    private RegisterVo registerVo;
    private UserVo userVo;
    private SchedulingVo scheduling;

}
