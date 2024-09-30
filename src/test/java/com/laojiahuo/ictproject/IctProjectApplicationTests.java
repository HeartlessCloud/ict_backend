/*
package com.laojiahuo.ictproject;

import com.laojiahuo.ictproject.PO.MessagePO;
import com.laojiahuo.ictproject.mapper.MessageMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
@Slf4j
class IctProjectApplicationTests {

    @Autowired
    private MessageMapper messageMapper;

    @Test
    public void testFindMessage() {
        MessagePO message = new MessagePO();
        message.setUserCode("testUser");
        message.setRole("user");
        message.setContent("Another message");
        message.setCreateTime(new java.util.Date());

        messageMapper.saveMessage(message);

        List<MessagePO> messages = messageMapper.findMessage("testUser");
        for(MessagePO messagePO:messages){
            log.info(messagePO.toString());
        }
    }

}
*/
