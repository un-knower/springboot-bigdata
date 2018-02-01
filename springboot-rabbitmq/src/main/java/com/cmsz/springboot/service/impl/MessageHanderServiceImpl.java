package com.cmsz.springboot.service.impl;

import com.cmsz.springboot.constans.PublicEnum;
import com.cmsz.springboot.dao.MessageBean;
import com.cmsz.springboot.dao.mapper.MessageHanderMapper;
import com.cmsz.springboot.service.IMessageHanderService;
import com.cmsz.springboot.service.RabbitMQService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * Created by le on 2018/2/1.
 */
@Service(value = "messageHanderService")
public class MessageHanderServiceImpl implements IMessageHanderService{

    private static final Logger logger = LoggerFactory.getLogger(MessageHanderServiceImpl.class);


    @Autowired
    private MessageHanderMapper messageHanderMapper;

    @Resource(name = "rabbitMQService")
    private RabbitMQService rabbitMQService;

    @Override
    public Boolean confirmMessage(String messageId,String code) {
         Boolean result=false;
         try {
             MessageBean messageBean= messageHanderMapper.queryByMessageId(messageId);
             messageBean.setStatus(code);
             messageHanderMapper.updateMessageBySelect(messageBean);
             result=true;
         }catch (Exception e){
             logger.error("消息体id{}更新失败{}",messageId,e.getMessage());
         }
         return result;
    }

    @Override
    public Boolean InsertOrUpdateMessageToDatabase(String messageId,String messageBody,String queue){
        Boolean InsertFlag=false;
        MessageBean initMessageBean= messageHanderMapper.queryByMessageId(messageId);
        if(null==initMessageBean){
            MessageBean messageBean=new MessageBean();
            messageBean.setMessageId(messageId);
            messageBean.setMessageBody(messageBody);
            messageBean.setConsumerQueue(queue);
            messageBean.setCreateTime(new Date());
            messageBean.setStatus(PublicEnum.UN_CONSUMER_STATUS.getCode());
            messageBean.setIsDead(PublicEnum.NO_DEAL.getCode());
            int result=messageHanderMapper.insertMessage(messageBean);
            if(result>0){InsertFlag=true;}
        }else{
            initMessageBean.addSendNums();
            initMessageBean.setUpdateTime(new Date());
            int updateCode=messageHanderMapper.updateMessageBySelect(initMessageBean);
            if(updateCode>0){InsertFlag=true;}
        }
        return InsertFlag;
    }

    @Scheduled(cron="0/30 * *  * * ? ")
    public void reSendUnconsumerMessage(){
        logger.info("====开始定时任务测试====");
        MessageBean messageBeanCondition=new MessageBean();
        messageBeanCondition.setStatus(PublicEnum.UN_CONSUMER_STATUS.getCode());
        List<MessageBean> messageBeans=messageHanderMapper.queryByMessageSelect(messageBeanCondition);
        logger.info("等待消费消息体数量【{}】",messageBeans.size());
        for(MessageBean messageBean:messageBeans){
            rabbitMQService.reSendRealTimeMessage(messageBean.getMessageId());
        }
    }
}
