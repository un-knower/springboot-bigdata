package com.cmsz.springboot.service;

import com.cmsz.springboot.constans.PublicEnum;
import com.cmsz.springboot.dao.mapper.MessageHanderMapper;
import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * Created by le on 2018/1/9.
 */
@Component(value = "rabbitMQConsumer")
public class RabbitMQConsumer {
    private static final Logger logger = LoggerFactory.getLogger(RabbitMQConsumer.class);

    /*交换机名称*/
    private static final String EXCHANGE_NAME="amq.fanout";

    @Resource(name = "realTimeChannel")
    private Channel realTimeChannel;

    @Resource(name = "alipyDelayChannel")
    private Channel alipyDelayChannel;

    @Resource(name = "delayChannel")
    private Channel delayChannel;

    @Resource(name ="rabbitMQService")
    private RabbitMQService rabbitMQService;

    @Autowired
    private MessageHanderMapper messageHanderMapper;

    @Resource(name = "messageHanderService")
    private IMessageHanderService messageHanderService;

    /*根据交换机接受消息体,取消息的最新*/
    public void consumerMessage() throws Exception{
        System.out.println("=========开始创建rabbitmq接受消息=========");
        //创建一个通道
        /*声明绑定的交换机及类型*/
        realTimeChannel.exchangeDeclare(EXCHANGE_NAME,"fanout",true);
        /*随机生成一个队列*/
        String queueName = realTimeChannel.queueDeclare().getQueue();
        /*绑定队列与交换机*/
        realTimeChannel.queueBind(queueName, EXCHANGE_NAME, "");
        QueueingConsumer consumer = new QueueingConsumer(realTimeChannel);
        // 指定消费队列,自动确认
        realTimeChannel.basicConsume(queueName, true, consumer);
        while (true)
        {
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            String message = new String(delivery.getBody());
            System.out.println("======="+message+"=======");
        }
    }

    /*根据交换机和queue取消息，保证消息不丢失*/
    public void consumerRealTimeMessage(String queue) {
        System.out.println("=========开始创建rabbitmq实时接受消息=========");
        //创建一个通道
        /*声明绑定的交换机及类型*/
        try {
           /*同一时刻只往特定的消费者发送一条消息*/
            realTimeChannel.basicQos(1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            final Consumer consumer = new DefaultConsumer(realTimeChannel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String messageId=properties.getMessageId();
                    String message = new String(body, "UTF-8");
                    /*初始化插入数据库中*/
                    messageHanderService.InsertOrUpdateMessageToDatabase(messageId,message,envelope.getExchange());
                    try {
                        logger.info("消息队列queue{}接收到消息体{}",queue,message);
//                        throw new Exception("异常测试");
                        /*业务逻辑完成后确认消息体*/
                        messageHanderService.confirmMessage(messageId,PublicEnum.SUCCESS_STATUS.getCode());
                        logger.info("消息体id【{}】已正常消费",messageId);
                        realTimeChannel.basicAck(envelope.getDeliveryTag(), false);
                    } catch (Exception e) {
                        logger.error("消息体id[{}]消费失败,将存储数据库",messageId);
                        Boolean result=messageHanderService.confirmMessage(messageId,PublicEnum.UN_CONSUMER_STATUS.getCode());
                        if(result){
                            realTimeChannel.basicAck(envelope.getDeliveryTag(), false);
                            logger.info("消息体{}存入数据库成功",messageId);
                        }
                    }
                }
            };
        try {
            realTimeChannel.basicConsume(queue, false, consumer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*接受延迟消息体，配置延迟消息体的channel*/
    public void consumerDelayMessage(String queue){
        System.out.println("=========开始接收rabbitmq延迟消息=========");
        String flag="延迟消息体";
        try {
           /*同一时刻只往特定的消费者发送一条消息*/
            delayChannel.basicQos(5);
        } catch (IOException e) {
            e.printStackTrace();
        }
        final Consumer consumer = new DefaultConsumer(delayChannel) {
            @Override
            public void handleDelivery(String consumerTag,
                                       Envelope envelope,
                                       AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                properties.getMessageId();
                try {
                    System.out.println("Worker1 "+flag+" Received '" + message + "'"+System.currentTimeMillis());
//                    rabbitMQService.sendRealTimeMessage("spms",message);
                    //doWork(message);
                }catch (Exception e){
                    delayChannel.abort();
                }finally {
                    System.out.println("消息体已消费："+message);
                    delayChannel.basicAck(envelope.getDeliveryTag(),false);
                }
            }
        };
        final boolean autoAck=false;
        //消息消费完成确认
        try {
            delayChannel.basicConsume(queue, autoAck, consumer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void consumeMessage(Channel channel,String queue,String flag){
        try {
           /*同一时刻只往特定的消费者发送一条消息*/
            channel.basicQos(5);
        } catch (IOException e) {
            e.printStackTrace();
        }
        final Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag,
                                       Envelope envelope,
                                       AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                Boolean sucess=true;
                try {
                    System.out.println("Worker1 "+flag+" Received '" + message + "'"+System.currentTimeMillis());
                    //doWork(message);
                }catch (Exception e){
                    channel.abort();
                }finally {
                    if(sucess){
                        System.out.println("消息体已消费："+message);
                        channel.basicAck(envelope.getDeliveryTag(),false);
                    }else{
                        System.out.println("word-deal");
                        rabbitMQService.sendDelayMessage(message,"5000");
                    }
                }
            }
        };
        final boolean autoAck=false;
        //消息消费完成确认
        try {
            channel.basicConsume(queue, autoAck, consumer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*根据交换机和queue取消息，保证消息不丢失*/
    public void consumerAlipyOrderMessage(String queue) {
        System.out.println("=========开始实时消费alipyOrder订单消息=========");
        //创建一个通道
        /*声明绑定的交换机及类型*/
        try {
           /*同一时刻只往特定的消费者发送一条消息*/
            realTimeChannel.basicQos(1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        final Consumer consumer = new DefaultConsumer(realTimeChannel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                boolean flag=false;
                try {
//                        doWork(message);
                    if(flag){
                        System.out.println(message+"已消费");
                    }else{
                        System.out.println("===="+message+"=====没有消费,将在20s后重新消费===");
                        rabbitMQService.sendAlipyDelayOrder(message,"20000");
                    }
                } finally {
                    realTimeChannel.basicAck(envelope.getDeliveryTag(), false);
                }
            }
        };
        boolean autoAck = false;
        try {
            realTimeChannel.basicConsume(queue, autoAck, consumer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*接受延迟消息体，配置延迟消息体的channel*/
    public void consumerAlipyOrDerDelayMessage(String queue){
        System.out.println("=========开始消费ali订单延迟消息=========");
        String flag="延迟消息体";
        try {
           /*同一时刻只往特定的消费者发送一条消息*/
            alipyDelayChannel.basicQos(5);
        } catch (IOException e) {
            e.printStackTrace();
        }
        final Consumer consumer = new DefaultConsumer(delayChannel) {
            @Override
            public void handleDelivery(String consumerTag,
                                       Envelope envelope,
                                       AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                try {
                    System.out.println("Worker1 "+flag+" Received '" + message + "'"+System.currentTimeMillis());
                }catch (Exception e){
                    alipyDelayChannel.abort();
                }finally {
                    System.out.println("消息体已消费："+message);
                    alipyDelayChannel.basicAck(envelope.getDeliveryTag(),false);
                }
            }
        };
        final boolean autoAck=false;
        //消息消费完成确认
        try {
            alipyDelayChannel.basicConsume(queue, autoAck, consumer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
