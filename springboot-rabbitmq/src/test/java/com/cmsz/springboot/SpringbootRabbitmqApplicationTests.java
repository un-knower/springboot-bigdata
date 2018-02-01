package com.cmsz.springboot;

import com.cmsz.springboot.dao.MessageBean;
import com.cmsz.springboot.dao.mapper.MessageHanderMapper;
import com.cmsz.springboot.service.RabbitMQConsumer;
import com.cmsz.springboot.service.RabbitMQService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringbootRabbitmqApplicationTests {

    @Resource(name = "rabbitMQService")
	private RabbitMQService rabbitMQService;
//
    @Resource(name = "rabbitMQConsumer")
	private RabbitMQConsumer rabbitMQConsumer;

    @Autowired
	private MessageHanderMapper messageHanderMapper;


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
	@Test
	public void queryMessage() throws Exception {
		MessageBean messageBean=messageHanderMapper.queryByMessageId("12");
		System.out.println(messageBean);
	}

}
