package com.cmsz.springboot.service.impl;

import com.cmsz.springboot.dao.MessageBean;
import com.cmsz.springboot.dao.mapper.MessageHanderMapper;
import com.cmsz.springboot.service.RabbitMQService;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

/**
 * Created by le on 2018/1/9.
 */
@Service(value = "rabbitMQService")
public class RabbitMQServiceImpl implements RabbitMQService {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQServiceImpl.class);

    private static  SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    @Value("${rabbitmq.exchange}")
    private  String EXCHANGE_NAME;

    @Resource(name = "realTimeChannel")
    private Channel realTimeChannel;

    @Resource(name = "baseChannel")
    private Channel baseChannel;

    @Resource(name = "delayChannel")
    private Channel delayChannel;

    @Resource(name = "alipyDelayChannel")
    private Channel alipyDelayChannel;

    @Value("${rabbitmq.exchage.delay}")
    private String exchage_delay;

    @Value("${rabbitmq.exchage.routing.key}")
    private String routingKey;

    @Autowired
    private MessageHanderMapper messageHanderMapper;

    @Override
    public boolean sendRealTimeMessage(String exchageName, String message) {
        Boolean result=false;
        //创建一个通道
        try {
            AMQP.BasicProperties.Builder builder = new AMQP.BasicProperties.Builder();
            builder.messageId(generateMessageUUID(exchageName));
            builder.deliveryMode(2);
            AMQP.BasicProperties properties = builder.build();
           /*声明绑定的交换机及类型*/
            realTimeChannel.basicPublish(exchageName, "", properties, message.getBytes("UTF-8"));
            result=true;
            logger.info("发送消息体: {}",message);
        } catch (IOException e) {
            e.printStackTrace();
            try {
                /*关闭连接*/
                realTimeChannel.close();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public void sendMessage(String message){
        //创建一个通道
        try {
        /*声明绑定的交换机及类型*/
//            channel.exchangeDeclare(EXCHANGE_NAME, "direct");
            baseChannel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes("UTF-8"));
            System.out.println("实时发送Producer Send +'" + message + "'");
        } catch (IOException e) {
            e.printStackTrace();
            try {
                /*关闭连接*/
                baseChannel.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (TimeoutException e1) {
                e1.printStackTrace();
            }
        }
    }

    @Override
    public void sendDelayMessage(String message, String ttl) {
        // 设置延时属性
        AMQP.BasicProperties.Builder builder = new AMQP.BasicProperties.Builder();
        // 持久性 non-persistent (1) or persistent (2)
        AMQP.BasicProperties properties = builder.expiration(ttl).deliveryMode(2).build();
        // routingKey =delay_queue 进行转发
        try {
            // 绑定路由
           /*发布消息体*/
            delayChannel.basicPublish("delay_queue", "delay_queue", properties, message.getBytes());
            System.out.println("sent 延迟 message: " + message + ",date:" + System.currentTimeMillis());
        } catch (IOException e) {
            e.printStackTrace();
            try {
                delayChannel.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (TimeoutException e1) {
                e1.printStackTrace();
            }
        }
    }

    @Override
    public void sendAlipyOrder(String queue,String message) {
      this.sendRealTimeMessage(queue,message);
    }

    @Override
    public void sendAlipyDelayOrder(String message, String ttl) {
        // 设置延时属性
        AMQP.BasicProperties.Builder builder = new AMQP.BasicProperties.Builder();
        AMQP.BasicProperties properties = builder.expiration(ttl).deliveryMode(2).build();
        // routingKey =delay_queue 进行转发
        try {
            // 绑定路由
           /*发布消息体*/
            alipyDelayChannel.basicPublish("delay_queue", "delay_queue", properties, message.getBytes());
            System.out.println("已发送延迟消息体=="+message+"==TTL时间为:"+ttl);
        } catch (IOException e) {
            e.printStackTrace();
            try {
                alipyDelayChannel.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (TimeoutException e1) {
                e1.printStackTrace();
            }
        }
    }

    @Override
    public void reSendRealTimeMessage(String messageId) {
        MessageBean messageBean=messageHanderMapper.queryByMessageId(messageId);
        if(null==messageBean){
            logger.error("消息体主键{}不存在",messageId);
        }else{
            String exchageName=messageBean.getConsumerQueue();
            String messageBody=messageBean.getMessageBody();
            //创建一个通道
            try {
                AMQP.BasicProperties.Builder builder = new AMQP.BasicProperties.Builder();
                builder.messageId(messageId);
                builder.deliveryMode(2);
                AMQP.BasicProperties properties = builder.build();
           /*声明绑定的交换机及类型*/
                realTimeChannel.basicPublish(exchageName, "", properties, messageBody.getBytes("UTF-8"));
                logger.info("发送消息主键{}成功",messageId);
            } catch (IOException e) {
                e.printStackTrace();
                try {
                /*关闭连接*/
                    realTimeChannel.close();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }

    }

    private String generateMessageUUID(String exchageName){
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        return exchageName+"-"+uuid;
    }

}
