package com.neuedu.hisweb.entity.enums;


//import com.baomidou.mybatisplus.core.enums.IEnum;   旧版本的写法

import com.baomidou.mybatisplus.annotation.IEnum;
// 或者
//import com.baomidou.mybatisplus.core.toolkit.IEnum;

public enum UserTypeEnum implements IEnum<Integer> {

    医院管理员(1, "医院管理员"),
    挂号收费员(2, "挂号收费员"),
    门诊医生(3,"门诊医生"),
    医技医生(4,"医技医生"),
    药房操作员(5,"药房操作员"),
    财务管理员(6,"财务管理员");

    UserTypeEnum(Integer code, String value) {
        this.code = code;
        this.value = value;
    }

    private final Integer code;

    private final String value;

    public Integer getValue() {
        return this.code;
    }


}
