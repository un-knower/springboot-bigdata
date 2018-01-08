package com.cmsz.springboot.bolt;

import com.cmsz.springboot.annotation.storm.StormBolt;
import com.cmsz.springboot.utils.PropertiesUtil;
import com.cmsz.springboot.utils.SpringUtil;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by le on 2018/1/5.
 */
@StormBolt(id="KafkaCustomBolt",name = "发往kafka消息体bolt",groupName = "scBeanBolt")
public class KafkaCustomBolt extends BaseRichBolt {

    private static Logger logger = LoggerFactory.getLogger(KafkaCustomBolt.class);

    private OutputCollector collector;

    private KafkaProducer<String,String> kafkaProducer;

    private String topic;

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.collector=outputCollector;
        kafkaProducer=SpringUtil.getBeanByName("kafkaProducer",KafkaProducer.class);
        logger.info("===初始化kafka生成者{}",kafkaProducer);
        this.topic= PropertiesUtil.getProperty("storm.kafka.topic.output");
    }

    @Override
    public void execute(Tuple tuple) {
        String key=tuple.getStringByField("key");
        String value=tuple.getStringByField("value");
        logger.info("发送消息体入kafka——topic:{}-key:{}-value:{}",topic,key,value);
        kafkaProducer.send(new ProducerRecord<String, String>(topic,key,value));
        collector.ack(tuple);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {

    }
}

