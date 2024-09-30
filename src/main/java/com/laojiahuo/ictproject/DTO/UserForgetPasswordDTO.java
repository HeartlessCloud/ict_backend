package com.laojiahuo.ictproject.DTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("用户忘记密码DTO")
@Data
public class UserForgetPasswordDTO {
    @ApiModelProperty("邮箱")
    private String email;

/*    @ApiModelProperty("发送验证码时的邮箱")
    private String preEmail;
    @ApiModelProperty("验证码")
    private String verifyCode;
    */

    @ApiModelProperty("用户输入的验证码")
    private String userVerifyCode;


    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("再次确认的密码")
    private String againPassword;
}
