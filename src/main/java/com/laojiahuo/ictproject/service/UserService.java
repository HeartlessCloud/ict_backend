package com.laojiahuo.ictproject.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.laojiahuo.ictproject.AO.UserAO;
import com.laojiahuo.ictproject.AO.UserUpdateRequestAO;
import com.laojiahuo.ictproject.DTO.UserForgetPasswordDTO;
import com.laojiahuo.ictproject.DTO.UserLoginByPasswordDTO;
import com.laojiahuo.ictproject.DTO.UserRegisterDTO;
import com.laojiahuo.ictproject.PO.UserPO;
import com.laojiahuo.ictproject.utils.JsonResult;

public interface UserService extends IService<UserPO> {

    UserPO getUserByCode(String senderCode);


    JsonResult sendEmailVerifyCode(String email);

//    void chatDelete(String chatCode);

    String getUserCodeByToken(String token);


    JsonResult userInfo(String userCode);

    JsonResult userInfoUpdate(String token, UserUpdateRequestAO request);

    JsonResult register(UserRegisterDTO request);

    JsonResult loginByPassword(UserLoginByPasswordDTO request);

    JsonResult loginByVerifyCode(UserAO request);

    JsonResult passwordForget(UserForgetPasswordDTO request);

    JsonResult loginByScan(String pid, String did, String createTime);

    JsonResult loginByScanListen(String pid, String createTime);

    JsonResult userPasswordUpdate(String userCode, String oldPassword, String newPassword, String confirmPassword);
}
