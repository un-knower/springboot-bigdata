package com.cmsz.springboot.service;

/**
 * Created by le on 2018/1/26.
 */
public interface IRabbitMQService {
    void sendMessage(String message);
}
