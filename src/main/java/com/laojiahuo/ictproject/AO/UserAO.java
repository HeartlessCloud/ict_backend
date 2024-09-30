package com.laojiahuo.ictproject.AO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("用户")
@Data
public class UserAO {

//    @ApiModelProperty("type, 0-注册 1-登录")
//    private Integer type;

    @ApiModelProperty("唯一code")
    private String userCode;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("学号")
    private String studentNum;

    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty("学校")
    private String school;

    @ApiModelProperty("发送验证码时的邮箱")
    private String preEmail;

    @ApiModelProperty("用户输入的验证码")
    private String userVerifyCode;

    @ApiModelProperty("验证码")
    private String verifyCode;

    @ApiModelProperty("头像")
    private String headshot;

    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("再次确认的密码")
    private String againPassword;

}
