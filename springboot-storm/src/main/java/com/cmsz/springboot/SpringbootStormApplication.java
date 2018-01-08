package com.cmsz.springboot;

import com.cmsz.springboot.service.storm.StormOperateServer;
import com.cmsz.springboot.utils.SpringUtil;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringbootStormApplication {

	private static Logger logger = LoggerFactory.getLogger(SpringbootStormApplication.class);

	private static ApplicationContext ac;

	private static final String  conf = "dispatcher-servlet.xml";

	public static void main(String[] args) throws Exception {
		logger.info("====进入主函数,初始化spring配置======log4j");
		SpringContextInit();
		ac.getBean("stormOperateService",StormOperateServer.class)
				.autoBuildStormTopology("springboot-storm","clouster");
		logger.info("====构建spring-storm完毕======log4j=={}");
	}

	private static synchronized  void SpringContextInit() {
		if (ac == null) {ac = new ClassPathXmlApplicationContext(conf);}
	}
}
