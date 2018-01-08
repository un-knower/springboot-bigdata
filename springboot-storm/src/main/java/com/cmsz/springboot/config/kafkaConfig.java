package com.cmsz.springboot.config;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * Created by le on 2017/11/24.
 */
@Configuration
public class kafkaConfig {

    @Value("${storm.kafka.bootstrap.service}")
    private String bootStrapService;


    private Properties buildKafkaProperties(){
        Properties props = new Properties();
        props.put("bootstrap.servers", bootStrapService);
        /*取最早的消息体，防止数据丢失*/
        props.put("enable.auto.commit", "false");
        props.put("auto.offset.reset", "earliest");
        props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "30000");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        return props;
    }

    @Bean(name = "kafkaConsumer")
     public KafkaConsumer<String,String> kafkaConsumer(){
        Properties properties=buildKafkaProperties();
        //限制每次批量消费的消息量
        properties.put("max.poll.records",100);
        return new KafkaConsumer<String, String>(properties);
     }

     @Bean(name = "kafkaProducer")
     public KafkaProducer<String,String> kafkaProducer(){
         Properties props = buildKafkaProperties();
         //配置每一批的大小为16M,第二行配置每隔10ms一发送
         props.put("batch.size", 16384);
         props.put("linger.ms", 10);
         return new KafkaProducer<String, String>(props);
     }

}
