package com.cmsz.springboot.config;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by le on 2018/1/9.
 */
@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.host}")
    private String host;

    @Value("${rabbitmq.port}")
    private String port;

    @Value("${rabbitmq.userName}")
    private String userName;

    @Value("${rabbitmq.password}")
    private String password;

    @Value("${rabbitmq.virtualHost}")
    private String virtualHost;


    @Bean(name = "rabbitMQConnection")
    public Connection connection(){
        //创建连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        Connection connection=null;
        //设置RabbitMQ相关信息
        factory.setHost(host);
        factory.setPort(Integer.valueOf(port));
        factory.setUsername(userName);
        factory.setPassword(password);
        factory.setVirtualHost(virtualHost);
        //创建一个新的连接
        try {
             connection = factory.newConnection();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return connection;
    }

}
