package com.cmsz.springboot.config;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.HashMap;
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

    @Value("${rabbitmq.exchage.routing.key}")
    private String routingKey;

    @Value("${rabbitmq.queue.input}")
    private String queue_input;


    @Value("${rabbitmq.queue.out}")
    private String queue_output;

    @Value("${rabbitmq.exchange}")
    private String exchangeName;

    @Value("${rabbitmq.exchage.delay}")
    private String exchange_delay;


    /*最基础的创建channel*/
    @Bean(name = "baseChannel")
    public Channel baseChannel(){
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
             Channel channel=connection.createChannel();
            return channel;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /*创建实时channel,消息体直接发布订阅，不经过交换机，防止数据丢失*/
    @Bean(name = "realTimeChannel")
    public Channel realTimeChannel(){
        Channel realTimeChannel=baseChannel();
        try {
            /*创建一个queue，没有则创建*/
            realTimeChannel.queueDeclare(queue_input, true, false, false, null);
            /*创建一个exchange*/
            realTimeChannel.exchangeDeclare(exchangeName, "fanout", true);
           /*绑定队列与交换机*/
            realTimeChannel.queueBind(queue_input, exchangeName, "");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return realTimeChannel;
    }

    /*创建延迟的channel,消息死信后将发布到exchangeName 这个交换机内，关键字为routingKey*/
    @Bean(name = "delayChannel")
    public Channel buildDelayChannel() throws IOException {
        Channel delayChannel=baseChannel();
        HashMap<String, Object> arguments = new HashMap<String, Object>();
        arguments.put("x-dead-letter-exchange", exchange_delay);
        arguments.put("x-dead-letter-routing-key", routingKey);
        // 声明队列
        /*声明一个delay_queue的队列和delay_queue的交换机，通过delay_queue关键字绑定将两者绑定一起*/
        delayChannel.queueDeclare("delay_queue", true, false, false, arguments);
        delayChannel.exchangeDeclare("delay_queue","direct",true);
        delayChannel.queueBind("delay_queue","delay_queue","delay_queue");
        // 绑定路由
        delayChannel.queueDeclare(queue_output, true, false, false, null);
        delayChannel.queueBind(queue_output, exchange_delay, routingKey);
        return delayChannel;
    }

    /*创建延迟的channel,消息死信后将发布到exchangeName 这个交换机内，关键字为routingKey*/
    @Bean(name = "alipyDelayChannel")
    public Channel alipyDelayChannel() throws IOException {
        Channel delayChannel=baseChannel();
        HashMap<String, Object> arguments = new HashMap<String, Object>();
        arguments.put("x-dead-letter-exchange", exchange_delay);
        arguments.put("x-dead-letter-routing-key", routingKey);
        // 声明队列
        /*声明一个delay_queue的队列和delay_queue的交换机，通过delay_queue关键字绑定将两者绑定一起*/
        delayChannel.queueDeclare("delay_queue", true, false, false, arguments);
        delayChannel.exchangeDeclare("delay_queue","direct",true);
        delayChannel.queueBind("delay_queue","delay_queue","delay_queue");
        // 绑定路由
        delayChannel.queueDeclare(queue_input, true, false, false, null);
        delayChannel.queueBind(queue_input, exchange_delay, routingKey);
        return delayChannel;
    }

}
