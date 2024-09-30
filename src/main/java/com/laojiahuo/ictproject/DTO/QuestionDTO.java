package com.laojiahuo.ictproject.DTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
@ApiModel("用户提问DTO")
@Data
public class QuestionDTO {
    @ApiModelProperty("内容")
    private String content;
    @ApiModelProperty("任务类型")
    private String type;
}

