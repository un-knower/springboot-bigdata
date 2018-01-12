package com.cmsz.springboot;

import com.cmsz.springboot.service.RabbitMQConsumer;
import com.cmsz.springboot.service.RabbitMQService;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.QueueingConsumer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringbootRabbitmqApplicationTests {

    @Resource(name = "rabbitMQService")
	private RabbitMQService rabbitMQService;

    @Resource(name = "rabbitMQConsumer")
	private RabbitMQConsumer rabbitMQConsumer;



	@Test
	public void contextLoads() throws Exception {
//		rabbitMQService.sendMessage("spms","测试-lile-1");
//		rabbitMQService.sendDelayMessage("测试-lile-1");
	}

	@Test
	public void testSend() throws IOException, TimeoutException {
		rabbitMQService.sendDelayMessage("lilessssssss","2000");
//		rabbitMQService.sendRealTimeMessage("spms","lile12xxx222");
	}

   @Test
	public void testDelayConsumer() throws Exception {
        rabbitMQConsumer.consumerRealTimeMessage("spms");
	}

}
