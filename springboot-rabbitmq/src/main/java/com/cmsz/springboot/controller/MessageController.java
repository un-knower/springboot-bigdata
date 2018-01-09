package com.cmsz.springboot.controller;

import com.cmsz.springboot.service.RabbitMQService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.awt.*;

/**
 * Created by le on 2018/1/9.
 */
@RestController
@RequestMapping("rabbitmq")
public class MessageController {

    @Autowired
    private RabbitMQService rabbitMQService;

    @RequestMapping(value = "sendMessage",produces = MediaType.TEXT_PLAIN_VALUE)
    public String sendMessage() throws Exception {
        rabbitMQService.sendMessage("spms","lile测试。。。。");
        return "ok";
    }

}
