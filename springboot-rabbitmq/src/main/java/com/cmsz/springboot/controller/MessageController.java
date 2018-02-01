package com.cmsz.springboot.controller;

import com.cmsz.springboot.controller.data.AjaxPageResult;
import com.cmsz.springboot.dao.MessageBean;
import com.cmsz.springboot.service.RabbitMQService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.awt.*;

/**
 * Created by le on 2018/1/9.
 */
@RestController
@RequestMapping("rabbitmq")
public class MessageController {

    @Autowired
    private RabbitMQService rabbitMQService;

    @RequestMapping(value = "sendDirectMessage",produces = MediaType.TEXT_PLAIN_VALUE,method = RequestMethod.GET)
    public String sendDirectMessage(@RequestParam(value = "message") String message) throws Exception {
        rabbitMQService.sendRealTimeMessage("amq.fanout",message);
//        rabbitMQService.sendDelayMessage(message,"5000");
        return "ok";
    }

    @RequestMapping(value = "sendMessage",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public AjaxPageResult sendMessage(@RequestBody MessageBean messageBean) throws Exception {
        if(StringUtils.isEmpty(messageBean.getConsumerQueue())){
            return AjaxPageResult.fail("消息体消息队列或者交换机不能为空",messageBean);
        }
        if(StringUtils.isEmpty(messageBean.getMessageBody())){
            return AjaxPageResult.fail("消息体消息不能为空",messageBean);
        }
        Boolean result=rabbitMQService.sendRealTimeMessage(messageBean.getConsumerQueue(),messageBean.getMessageBody());
        System.out.println(result);
        if(result){
            return AjaxPageResult.success("消息发送成功");
        }else {
             return AjaxPageResult.fail("消息发送失败",messageBean);
        }
    }

    @RequestMapping(value = "alipyOrder",produces = MediaType.TEXT_PLAIN_VALUE)
    public String alipyOrder(@RequestParam(value = "message") String message){
        rabbitMQService.sendAlipyOrder("spms",message);
        return message+"已下订单";
    }

}
