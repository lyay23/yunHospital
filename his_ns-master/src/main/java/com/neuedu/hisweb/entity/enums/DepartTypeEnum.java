package com.neuedu.hisweb.entity.enums;

import com.baomidou.mybatisplus.annotation.IEnum;

public enum DepartTypeEnum implements IEnum<Integer> {

    临床(1, "临床"),
    医技(2, "医技"),
    财务(3,"财务"),
    行政(4,"行政"),
    其他(5,"其他");

    DepartTypeEnum(Integer code, String value) {
        this.code = code;
        this.value = value;
    }

    private final Integer code;

    private final String value;

    public Integer getValue() {
        return this.code;
    }

}