package com.cmsz.springboot.service;

/**
 * Created by le on 2018/2/1.
 */
public interface IMessageHanderService {
    /*
    * 确认消息已成功消费
    * */
    Boolean confirmMessage(String messageId,String code);

    /*
    * 初始化插入数据库中，备份消息体
    * */
    Boolean InsertOrUpdateMessageToDatabase(String messageId,String messageBody,String queue);
}
