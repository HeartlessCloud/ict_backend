package com.laojiahuo.ictproject.DTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
@ApiModel("用户密码登录DTO")
@Data
public class UserLoginByPasswordDTO {

        @ApiModelProperty("邮箱")
        private String email;

        @ApiModelProperty("密码")
        private String password;
}
