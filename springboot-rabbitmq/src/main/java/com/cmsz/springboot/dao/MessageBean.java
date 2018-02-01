package com.cmsz.springboot.dao;

import lombok.Data;

import java.util.Date;

/**
 * Created by le on 2018/1/31.
 */
@Data
public class MessageBean {
    /*消息体id*/
    private String messageId;
    /*消息主体*/
    private String messageBody;
    /*发送次数*/
    private Integer sendNums;
    /*创建时间*/
    private Date createTime;
    /*更新时间*/
    private Date updateTime;
    /*是否已死亡0标识正常，1表示死亡*/
    private String isDead;
    /*消息体状态*/
    private String status;
    /*消息队列名称*/
    private String consumerQueue;

    public void addSendNums(){
        if(null==sendNums){
            sendNums=1;
        }else{
            sendNums++;
        }
    }
}
