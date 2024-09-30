package com.laojiahuo.ictproject.PO;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@TableName("tb_chat_message")
@Data
@ApiModel("聊天室对话")
public class ChatMessagePO {
    @ApiModelProperty("信息id")
    @TableId(type = IdType.AUTO)
    private String messageId;
    @ApiModelProperty("信息内容")
    private String messageContent;
    @ApiModelProperty("发送者编号")
    private String sendUserCode;
    @ApiModelProperty("发送时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date sendTime;
}
