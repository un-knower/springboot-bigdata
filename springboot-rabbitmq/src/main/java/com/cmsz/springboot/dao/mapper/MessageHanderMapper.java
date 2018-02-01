package com.cmsz.springboot.dao.mapper;

import com.cmsz.springboot.dao.MessageBean;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by le on 2018/1/31.
 */
@Repository
public interface MessageHanderMapper {

    int insertMessage(MessageBean messageBean);

    MessageBean queryByMessageId(String messageId);

    int updateMessageBySelect(MessageBean messageBean);

    void deleteMessageByMessageId(String messageId);

    /*
    根据条件查询
    */
    List<MessageBean> queryByMessageSelect(MessageBean messageBean);
}
