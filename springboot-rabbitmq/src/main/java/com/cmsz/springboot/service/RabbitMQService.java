package com.cmsz.springboot.service;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeoutException;

/**
 * Created by le on 2018/1/9.
 */
public interface RabbitMQService {
    /*带queue的发送消息体*/
    boolean sendRealTimeMessage(String exchangeName,String message);

    /*不带queue的发送消息体，发送至交互机内，队下一个列队中只要绑定了该消息体，就可以接受*/
    void sendMessage(String message);

    /*延迟发送消息体*/
    void sendDelayMessage(String message,String ttl);

    /*延迟发送消息体*/
    void sendAlipyOrder(String queue,String message);

    /*发送alipay订单延迟消息*/
    void sendAlipyDelayOrder(String message,String ttl);

    void reSendRealTimeMessage(String messageId);
}
