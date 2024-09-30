package com.laojiahuo.ictproject.AO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * 用于修改用户信息
 */
@ApiModel("用户")
@Data
public class UserUpdateRequestAO {

    @ApiModelProperty("唯一code")
    private String userCode;

    @ApiModelProperty("学号")
    private String studentNum;

    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty("学校")
    private String school;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("头像")
    private MultipartFile headshot;

    @ApiModelProperty("密码")
    private String password;

    public UserUpdateRequestAO(MultipartFile headshot, String username,String studentNum,String school) {
        this.headshot = headshot;
        this.username = username;
        this.school = school;
        this.studentNum = studentNum;
    }
}
