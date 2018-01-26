package com.cmsz.springboot;

import com.cmsz.springboot.service.RabbitMQConsumer;
import com.cmsz.springboot.service.RabbitMQService;
import com.cmsz.springboot.service.RedisLockService;
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
//
    @Resource(name = "rabbitMQConsumer")
	private RabbitMQConsumer rabbitMQConsumer;

	@Resource(name = "redisLockService")
	private RedisLockService redisLockService;


	@Test
	public void contextLoads() throws Exception {
		Boolean flag=redisLockService.tryLock("lile",5);
		if(flag){
			System.out.println("xxxxxxx");
		}else{
			System.out.println("已");
		}
	}

	@Test
	public void testSend() throws IOException, TimeoutException {
//		rabbitMQService.sendDelayMessage("lilessssssss","2000");
		rabbitMQService.sendRealTimeMessage("spms","lzz1exxx222");
	}
//
   @Test
	public void testDelayConsumer() throws Exception {
        rabbitMQConsumer.consumerRealTimeMessage("spms");
	}

}
