package com.cmsz.springboot.config;

import com.cmsz.springboot.utils.SpringUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * Created by le on 2018/1/26.
 */
public class RabbitMQConfig {

    private String hostname;
    private String port;
    private String userName;
    private String virtualHost;
    private String password;
    private String queue_input;
    private String exchangeName;

    private Channel realTimeChannel=null;


    public RabbitMQConfig(String hostname,String port,String userName,String virtualHost,String password,String queue_input,String exchangeName){
        this.hostname=hostname;
        this.port=port;
        this.userName=userName;
        this.virtualHost=virtualHost;
        this.password=password;
        this.queue_input=queue_input;
        this.exchangeName=exchangeName;
    }

    public Channel initRabbitMQChannel(){
        Channel realTimeChannel=null;
        Connection connection = null;
        //创建连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        //设置RabbitMQ相关信息
        factory.setHost(hostname);
        factory.setPort(Integer.valueOf(port));
        factory.setUsername(userName);
        factory.setPassword(password);
        factory.setVirtualHost(virtualHost);
        //创建一个新的连接
        try {
            connection = factory.newConnection();
            realTimeChannel = connection.createChannel();
            /*创建一个queue，没有则创建*/
            realTimeChannel.queueDeclare(queue_input, true, false, false, null);
            /*创建一个exchange*/
            realTimeChannel.exchangeDeclare(exchangeName, "fanout", true);
           /*绑定队列与交换机*/
            realTimeChannel.queueBind(queue_input, exchangeName, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return realTimeChannel;
    }


}
