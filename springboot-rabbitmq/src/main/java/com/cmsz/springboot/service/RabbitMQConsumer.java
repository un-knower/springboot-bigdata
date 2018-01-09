package com.cmsz.springboot.service;

import com.rabbitmq.client.*;
import com.rabbitmq.client.impl.AMQImpl;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by le on 2018/1/9.
 */
@Component
public class RabbitMQConsumer {

    private static String EXCHANGE_NAME="HYTC";

    public void consumerMessage() throws IOException, TimeoutException, InterruptedException {
        System.out.println("=========开始创建rabbitmq接受消息=========");
        //创建连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        //设置RabbitMQ相关信息
        factory.setHost("192.168.5.60");
        factory.setPort(5671);
        factory.setUsername("lile");
        factory.setPassword("123");
        factory.setVirtualHost("/hadoop");
        //创建一个新的连接
        Connection connection = factory.newConnection();
        //创建一个通道
        Channel channel = connection.createChannel();
        /*声明绑定的交换机及类型*/
        channel.exchangeDeclare(EXCHANGE_NAME,"fanout");
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, EXCHANGE_NAME, "");
        /*每次只允许消费一条消息*/
        channel.basicQos(1);
        final Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag,
                                       Envelope envelope,
                                       AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println("Worker1  Received '" + message + "'");
                try {
                    throw  new Exception();
                    //doWork(message);
                }catch (Exception e){
                    channel.abort();
                }finally {
                    System.out.println("Worker1 Done");
                    channel.basicAck(envelope.getDeliveryTag(),false);
                }
            }
        };
        boolean autoAck=false;
        //消息消费完成确认
        channel.basicConsume(EXCHANGE_NAME, autoAck, consumer);
    }
}
