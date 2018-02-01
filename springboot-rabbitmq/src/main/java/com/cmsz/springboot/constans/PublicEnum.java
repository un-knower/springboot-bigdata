package com.cmsz.springboot.constans;

/**
 * Created by le on 2018/2/1.
 */
public enum PublicEnum {

    SUCCESS_STATUS("1","成功"),
    UN_CONSUMER_STATUS("0","未消费"),
    DEAL("1","消息死亡"),
    NO_DEAL("0","消息没有死亡");


    private String name;

    private String code;

     PublicEnum(String code,String name){
        this.name=name;
        this.code=code;
    }

    public String getCode(){return code;}
}
