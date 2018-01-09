package com.cmsz.springboot;

import com.cmsz.springboot.service.RabbitMQConsumer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@SpringBootApplication
public class SpringbootRabbitmqApplication {

	public static void main(String[] args) throws InterruptedException, TimeoutException, IOException {
		ApplicationContext ac=SpringApplication.run(SpringbootRabbitmqApplication.class, args);
	    ac.getBean(RabbitMQConsumer.class).consumerMessage();
	}
}
