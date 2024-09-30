package com.laojiahuo.ictproject.VO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class UserTokenVO {

    @ApiModelProperty("唯一code")
    private String userCode;

    @ApiModelProperty("邮箱")
    private String email;

//    @ApiModelProperty("用户名")
//    private String username;

    @ApiModelProperty("token")
    private String token;

}
