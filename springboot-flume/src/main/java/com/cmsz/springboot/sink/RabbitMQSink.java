package com.cmsz.springboot.sink;

import com.cmsz.springboot.config.RabbitMQConfig;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.rabbitmq.client.*;
import org.apache.flume.*;
import org.apache.flume.Channel;
import org.apache.flume.conf.Configurable;
import org.apache.flume.sink.AbstractSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * Created by le on 2017/10/21.
 */
public class RabbitMQSink extends AbstractSink implements Configurable {

    private Logger LOG = LoggerFactory.getLogger(RabbitMQSink.class);

    private String hostname;
    private String port;
    private String userName;
    private String virtualHost;
    private String password;
    private String queue_input;
    private String exchangeName;

    private com.rabbitmq.client.Channel realTimeChannel;

    @Override
    public synchronized void start() {
        super.start();
        RabbitMQConfig rabbitMQConfig=new RabbitMQConfig(hostname,port,userName,virtualHost,password,queue_input,exchangeName);
        realTimeChannel=rabbitMQConfig.initRabbitMQChannel();
    }

    @Override
    public synchronized void stop() {
        super.stop();
        if (realTimeChannel != null) {
            try {
                realTimeChannel.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Status process() throws EventDeliveryException {
        Status status = null;
        Channel ch = getChannel();
        Transaction txn = ch.getTransaction();
        txn.begin();
        try {
            Event event = ch.take();
            String content = new String(event.getBody());
            /*业务逻辑*/
            if(content.contains("ERROR")) {
                sendMessage(content);
            }
            txn.commit();
            status = Status.READY;
        }catch (Exception  e) {
            txn.rollback();
            status = Status.BACKOFF;
        }finally {
            txn.close();
        }
        return status;
    }

    public void configure(Context context) {
        hostname = context.getString("hostname");
        Preconditions.checkNotNull(hostname, "hostname must be set!!");
        port = context.getString("port");
        Preconditions.checkNotNull(port, "port must be set!!");
        userName = context.getString("userName");
        Preconditions.checkNotNull(userName, "user must be set!!");
        password = context.getString("password");
        Preconditions.checkNotNull(password, "password must be set!!");
        virtualHost = context.getString("virtualHost");
        Preconditions.checkNotNull(virtualHost, "virtualHost must be set!!");
        queue_input = context.getString("queue_input");
        Preconditions.checkNotNull(queue_input, "queue_input must be set!!");
        exchangeName = context.getString("exchangeName");
        Preconditions.checkNotNull(exchangeName, "exchangeName must be set!!");
    }


    private void sendMessage(String message){
        //创建一个通道
        try {
            /*声明绑定的交换机及类型*/
            realTimeChannel.basicPublish(exchangeName, "", null, message.getBytes("UTF-8"));
            System.out.println("Producer Send +'" + message + "'");
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
