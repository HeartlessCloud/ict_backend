package com.laojiahuo.ictproject.mapper;

import com.laojiahuo.ictproject.PO.MessagePO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageMapper {

    List<MessagePO> findMessage(String userCode);

    void saveMessage(MessagePO messagePO);
}
