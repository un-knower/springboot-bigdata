package com.cmsz.springboot.enums;

/**
 * Created by le on 2018/1/4.
 */
public enum GroupTypeEnums {

    SHUFFLE("随机分组","shuffle");
    /**
     * 枚举值
     */
    private final String name;

    /**
     * 枚举描述
     */
    private final String value;

    private GroupTypeEnums(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getValue(){
        return value;
    }

}
