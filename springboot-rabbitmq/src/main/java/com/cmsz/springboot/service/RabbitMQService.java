package com.cmsz.springboot.service;

import java.io.IOException;

/**
 * Created by le on 2018/1/9.
 */
public interface RabbitMQService {

    void sendMessage(String topic,String message) throws Exception;

    void sendMessage(String message);

}
