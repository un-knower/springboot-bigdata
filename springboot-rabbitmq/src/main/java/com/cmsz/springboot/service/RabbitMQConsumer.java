package com.cmsz.springboot.service;

import com.rabbitmq.client.*;
import com.rabbitmq.client.impl.AMQImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by le on 2018/1/9.
 */
@Component
public class RabbitMQConsumer {

    /*交换机名称*/
    private static final String EXCHANGE_NAME="HYTC";

    private static final String QUEUE_NAME="spms";

    @Resource(name = "rabbitMQConnection")
    private Connection connection;

    public void consumerMessage() throws IOException, TimeoutException, InterruptedException {
        System.out.println("=========开始创建rabbitmq接受消息=========");
        //创建一个通道
        Channel channel = connection.createChannel();
        /*声明绑定的交换机及类型*/
        channel.exchangeDeclare(EXCHANGE_NAME,"fanout");
        /*随机生成一个队列*/
//        String queueName = channel.queueDeclare().getQueue();
        /*绑定队列与交换机*/
        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "");
        /*每次只允许消费一条消息*/
        channel.basicQos(1);
        final Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag,
                                       Envelope envelope,
                                       AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                try {
                    System.out.println("Worker1  Received '" + message + "'");
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
        channel.basicConsume(QUEUE_NAME, autoAck, consumer);
    }
}
