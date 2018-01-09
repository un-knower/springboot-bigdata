package com.cmsz.springboot;

import com.cmsz.springboot.service.RabbitMQService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringbootRabbitmqApplicationTests {

	@Autowired
	private RabbitMQService rabbitMQService;

	@Test
	public void contextLoads() throws Exception {
		rabbitMQService.sendMessage("spms","测试-lile-1");
	}

}
