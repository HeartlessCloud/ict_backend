package com.laojiahuo.ictproject.DTO;

import com.laojiahuo.ictproject.PO.ChatMessagePO;
import lombok.Data;

import java.util.List;

/**
 * 需要给用户的信息
 */
@Data
public class WsInitData {
    // 上线到现在的消息列表
    private List<ChatMessagePO> chatMessagePOList;
}
