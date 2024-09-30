package com.laojiahuo.ictproject.service.impl;

import com.laojiahuo.ictproject.PO.MessagePO;
import com.laojiahuo.ictproject.mapper.MessageMapper;
import com.laojiahuo.ictproject.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
public class MessageServiceImpl implements MessageService {
    @Autowired
    private MessageMapper messageMapper;

    @Override
    public List<MessagePO> findMessage(String userCode) {
        if(userCode==null || "".equals(userCode)){
            return null;
        }
        return messageMapper.findMessage(userCode);
    }

    @Override
    public void saveMessage(MessagePO messagePO) {
        if(messagePO.getUserCode()==null || "".equals(messagePO.getUserCode())){
            return;
        }
        messageMapper.saveMessage(messagePO);
    }
}
