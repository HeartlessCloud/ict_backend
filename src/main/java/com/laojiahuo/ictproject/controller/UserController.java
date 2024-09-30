package com.laojiahuo.ictproject.controller;


import com.alibaba.fastjson.JSON;
import com.laojiahuo.ictproject.AO.UserAO;
import com.laojiahuo.ictproject.AO.UserUpdateRequestAO;
import com.laojiahuo.ictproject.DTO.UserForgetPasswordDTO;
import com.laojiahuo.ictproject.DTO.UserLoginByPasswordDTO;
import com.laojiahuo.ictproject.DTO.UserRegisterDTO;
import com.laojiahuo.ictproject.service.UserService;
import com.laojiahuo.ictproject.utils.JsonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@RestController
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;


    @ApiOperation("用户注册")
    @Transactional(rollbackFor = Exception.class)
    @PostMapping("/user/register")
    public JsonResult register(@RequestBody UserRegisterDTO request) {
        log.info("UserController register request:[{}]", request);
        JsonResult response = userService.register(request);

        return response;
    }


    @ApiOperation("用户快捷登录，验证码登录或注册")
    @Transactional(rollbackFor = Exception.class)
    @PostMapping("/user/login/by/verifyCode")
    public JsonResult loginByVerifyCode(@RequestBody UserAO request) {
        log.info("UserController loginByVerifyCode request:[{}]", request);
        JsonResult response = userService.loginByVerifyCode(request);

        return response;
    }

    @ApiOperation("密码登录")
    @Transactional(rollbackFor = Exception.class)
    @PostMapping("/user/login/by/password")
    public JsonResult loginByPassword(@RequestBody UserLoginByPasswordDTO request) {
        log.info("UserController loginByPassword request:[{}]", request);
        JsonResult response = userService.loginByPassword(request);

        return response;
    }

    @ApiOperation("忘记密码，重设密码")
    @Transactional(rollbackFor = Exception.class)
    @PostMapping("/user/password/forget")
    public JsonResult passwordForget(@RequestBody UserForgetPasswordDTO request) {
        log.info("UserController passwordForget request:[{}]", request);
        JsonResult response = userService.passwordForget(request);

        return response;
    }

    @ApiOperation("发邮箱验证码")
    @GetMapping("/user/send/email")
    public JsonResult sendEmailVerifyCode(@Param("email") String email) {
        log.info("UserController sendEmailVerifyCode email:[{}]", email);
        JsonResult response = userService.sendEmailVerifyCode(email);

        return response;
    }

    @ApiOperation("用户信息（请求头带token）")
    @GetMapping("/user/info")
    public JsonResult userInfo(HttpServletRequest httpServletRequest) {
        String token = httpServletRequest.getHeader("Authorization");
        log.info("userController userInfo token:[{}]", token);
        if (token == null || "".equals(token)) {
            return JsonResult.error(401, "请先登录");
        }
        String userCode = userService.getUserCodeByToken(token);
        JsonResult response = userService.userInfo(userCode);

        return response;
    }

    @ApiOperation("用户信息修改")
    @PostMapping("/user/info/update")
    public JsonResult userInfoUpdate(HttpServletRequest httpServletRequest,
//                                     @RequestParam(value = "headshot", required = false) MultipartFile headshot,
                                     @RequestParam(value = "username", required = false) String username,
                                     @RequestParam(value = "school", required = false) String school,
                                     @RequestParam(value = "studentNum", required = false) String studentNum) {
        MultipartFile headshot = null;
        UserUpdateRequestAO request = new UserUpdateRequestAO(headshot, username,studentNum,school);
        log.info("userController userInfoUpdate request:[{}]", request);
        String token = httpServletRequest.getHeader("Authorization");
        log.info("userController userInfoUpdate token:[{}]", token);
        JsonResult response = userService.userInfoUpdate(token, request);

        return response;
    }

    @ApiOperation("用户密码修改")
    @GetMapping("/user/password/update")
    public JsonResult userPasswordUpdate(HttpServletRequest httpServletRequest,
                                     @RequestParam(value = "oldPassword", required = true) String oldPassword,
                                     @RequestParam(value = "newPassword", required = true) String newPassword,
                                     @RequestParam(value = "confirmPassword", required = true) String confirmPassword) {


        log.info("userController userPasswordUpdate oldPassword:[{}], newPassword:[{}], confirmPassword:[{}]", oldPassword, newPassword, confirmPassword);
        String token = httpServletRequest.getHeader("Authorization");
        log.info("userController userPasswordUpdate token:[{}]", token);
        String userCode = userService.getUserCodeByToken(token);
        JsonResult response = userService.userPasswordUpdate(userCode, oldPassword, newPassword, confirmPassword);

        return response;
    }

    @ApiOperation("扫码登录")
    @GetMapping("/user/login/by/scan")
    public JsonResult loginByScan(@RequestParam(value = "pid", required = false) String pid, @RequestParam(value = "did", required = false) String did, @RequestParam(value = "createTime", required = false) String createTime) {
        log.info("UserController loginByScan pid:[{}], did:[{}], createTime:[{}]", pid, did, createTime);
        JsonResult response = userService.loginByScan(pid, did, createTime);

        return response;
    }

    @ApiOperation("监听扫码登录")
    @GetMapping(value = "/user/login/by/scan/listen")
    public SseEmitter loginByScanListen(@RequestParam(value = "pid", required = false) String pid, @RequestParam(value = "createTime", required = false) String createTime) {
        log.info("UserController loginByScanListen pid:[{}], createTime:[{}]", pid, createTime);
        SseEmitter sseEmitter = new SseEmitter();// 创建一个 SSE（服务器发送事件）对象
        JsonResult response = userService.loginByScanListen(pid, createTime);// 监听登录结果
        try {
            System.out.println("-------response-------->" + response);
            sseEmitter.send(SseEmitter.event().data(JSON.toJSONString(response)));// 将结果返回前端
            sseEmitter.complete();// 结束 SSE

        } catch (IOException e) {
            throw new RuntimeException();
        }
        sseEmitter.onTimeout(() -> {

        });
        log.info("UserController loginByScanListen --> over");

        return sseEmitter;
    }

// 最后登录时间
}
