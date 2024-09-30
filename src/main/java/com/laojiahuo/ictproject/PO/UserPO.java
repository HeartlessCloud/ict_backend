package com.laojiahuo.ictproject.PO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;


@TableName("tb_user")
@ApiModel("用户")
@Data
public class UserPO {

    @TableId(type = IdType.AUTO)
    private Integer id;

    // user_+id组成
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
    private String headshot;

    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("1-已删除 0-未删除")
    private Integer isDeleted;

    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @ApiModelProperty("更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    @ApiModelProperty("最后登录时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastLoginTime;

    @ApiModelProperty("最后离线时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastOffTime;
}
