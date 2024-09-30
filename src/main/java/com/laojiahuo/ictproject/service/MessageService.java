package com.laojiahuo.ictproject.service;

import com.laojiahuo.ictproject.PO.MessagePO;

import java.util.List;

public interface MessageService{
    List<MessagePO> findMessage(String userCode);
    void saveMessage(MessagePO messagePO);

}
