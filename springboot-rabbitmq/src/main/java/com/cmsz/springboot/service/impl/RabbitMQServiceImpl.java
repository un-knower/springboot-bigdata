package com.cmsz.springboot.service.impl;

import com.cmsz.springboot.service.RabbitMQService;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by le on 2018/1/9.
 */
@Service
public class RabbitMQServiceImpl implements RabbitMQService {

    private static String EXCHANGE_NAME="HYTC";

    @Resource(name = "rabbitMQConnection")
    private Connection connection;

    @Override
    public void sendMessage(String topic, String message) throws Exception {
        //创建一个通道
        Channel channel = connection.createChannel();
        /*声明绑定的交换机及类型*/
        channel.exchangeDeclare(EXCHANGE_NAME,"fanout");
        //  声明一个队列
//        channel.queueDeclare(EXCHANGE_NAME, false, false, false, null);
        //发送消息到队列中
        try {
            channel.basicPublish(EXCHANGE_NAME, topic, null, message.getBytes("UTF-8"));
            System.out.println("Producer Send +'" + message + "'");
        }catch (Exception e){
            e.getCause();
            //关闭通道和连接
            channel.close();
            connection.close();
        }
    }

    @Override
    public void sendMessage(String message) throws IOException, TimeoutException {
        //创建一个通道
        Channel channel = connection.createChannel();
        /*声明绑定的交换机及类型*/
        channel.exchangeDeclare(EXCHANGE_NAME,"fanout");
        //  声明一个队列
//        channel.queueDeclare(EXCHANGE_NAME, false, false, false, null);
        //发送消息到队列中
        try {
            channel.basicPublish(EXCHANGE_NAME,"", null, message.getBytes("UTF-8"));
            System.out.println("Producer Send +'" + message + "'");
        }catch (Exception e){
            e.getCause();
            //关闭通道和连接
            channel.close();
            connection.close();
        }
    }
}
