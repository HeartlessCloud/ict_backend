package com.laojiahuo.ictproject.PO;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@TableName("tb_message")
@Data
@ApiModel("大模型对话")
public class MessagePO {
    @ApiModelProperty("用户code")
    private String userCode;
    @ApiModelProperty("角色")
    private String role;
    @ApiModelProperty("内容")
    private String content;
    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
}
