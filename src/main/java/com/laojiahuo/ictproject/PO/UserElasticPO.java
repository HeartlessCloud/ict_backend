package com.laojiahuo.ictproject.PO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;


@ApiModel("索引库用户")
@Data
public class UserElasticPO {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("学号")
    private String studentNum;

    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty("学校")
    private String school;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("头像")
    private String headshot;

    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

}
