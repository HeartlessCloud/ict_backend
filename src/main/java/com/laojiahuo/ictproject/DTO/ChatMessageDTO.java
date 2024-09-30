package com.laojiahuo.ictproject.DTO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("聊天室对话")
public class ChatMessageDTO {
    @ApiModelProperty("信息内容")
    private String messageContent;
    @ApiModelProperty("发送者编号")
    private String sendUserCode;
}
