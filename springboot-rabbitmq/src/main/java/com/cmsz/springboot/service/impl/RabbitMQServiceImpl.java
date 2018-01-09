package com.cmsz.springboot.service.impl;

import com.cmsz.springboot.service.RabbitMQService;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Created by le on 2018/1/9.
 */
@Service
public class RabbitMQServiceImpl implements RabbitMQService {

    private static String EXCHANGE_NAME="HYTC";

    @Override
    public void sendMessage(String topic, String message) throws Exception {
        //创建连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        //设置RabbitMQ相关信息
        factory.setHost("192.168.5.60");
//        factory.setPort(5671);
//        factory.setUsername("lile");
//        factory.setPassword("123");
//        factory.setVirtualHost("/hadoop");
        //创建一个新的连接
        Connection connection = factory.newConnection();
        //创建一个通道
        Channel channel = connection.createChannel();
        /*声明绑定的交换机及类型*/
        channel.exchangeDeclare(EXCHANGE_NAME,"fanout");
        //  声明一个队列
        //channel.queueDeclare(QUEUE_NAME, false, false, false, null);
//        String message = "Hello RabbitMQ";
        //发送消息到队列中
        channel.basicPublish(EXCHANGE_NAME, topic, null, message.getBytes("UTF-8"));
        System.out.println("Producer Send +'" + message + "'");
        //关闭通道和连接
        channel.close();
        connection.close();
    }

    @Override
    public void sendMessage(String message) {

    }
}
