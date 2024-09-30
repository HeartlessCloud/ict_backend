package com.laojiahuo.ictproject.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.benmanes.caffeine.cache.Cache;
import com.laojiahuo.ictproject.AO.UserAO;
import com.laojiahuo.ictproject.DTO.UserForgetPasswordDTO;
import com.laojiahuo.ictproject.VO.UserTokenVO;
import com.laojiahuo.ictproject.AO.UserUpdateRequestAO;
import com.laojiahuo.ictproject.DTO.UserLoginByPasswordDTO;
import com.laojiahuo.ictproject.DTO.UserRegisterDTO;
import com.laojiahuo.ictproject.PO.UserPO;
import com.laojiahuo.ictproject.mapper.UserMapper;
import com.laojiahuo.ictproject.service.UserService;
import com.laojiahuo.ictproject.utils.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper,UserPO> implements UserService {



    private Object lock = new Object();


    @Resource
    private RabbitTemplate rabbitTemplate;


    @Autowired
    private Cache<String, Object> caffeineCache;


    @Resource
    private UserMapper userMapper;

    @Lazy
    @Autowired
    private MinioUtil minioUtil;


    @Override
    public UserPO getUserByCode(String senderCode) {
        log.info("UserServiceImpl getUserByCode senderCode:[{}]", senderCode);
        UserPO userPO = userMapper.getUserByCode(senderCode);
        log.info("UserServiceImpl getUserByCode userPO:[{}]", userPO);

        return userPO;
    }





    @Override
    public JsonResult register(UserRegisterDTO userRegisterDTO) {
        log.info("UserServiceImpl login userRegisterDTO:[{}]", userRegisterDTO);
        if (!(userRegisterDTO.getUsername() != null && userRegisterDTO.getUsername().length() <= 50)) {
            log.info("UserServiceImpl login 用户名不能为空，长度最大为50");
            return JsonResult.error("用户名不能为空，长度最大为50");
        }
        // 查询数据库是否存在该用户（有则直接登录，并生成token）
        UserPO userPO = userMapper.getUserByEmail(userRegisterDTO.getEmail());
        // 注册
        if (Objects.isNull(userPO)) {
            Map<String,String> cacheInfo = (Map<String,String>) caffeineCache.getIfPresent(userRegisterDTO.getEmail());
            if(cacheInfo==null){
                log.info("UserServiceImpl register 缓存中没有验证码数据，请先发送验证码");
                return JsonResult.error(500,"请发送验证码");
            }
            String verifyCode = cacheInfo.get("verifyCode");
            String PreEmail = cacheInfo.get("preEmail");

            // 判断邮箱格式
            String userEmail = userRegisterDTO.getEmail();
            Boolean emailIsLegal = (userEmail != null && userEmail.matches("^[1-9][0-9]{4,10}@qq\\.com$"));
            if (emailIsLegal) {
                if (verifyCode == null || "".equals(verifyCode)) {
                    return JsonResult.error(500, "验证码错误");
                }
                if (!PreEmail.equals(MD5Util.encrypt(userRegisterDTO.getEmail()))) {
                    log.info("UserServiceImpl register 邮箱有改动，不一致！");
                    return JsonResult.error("邮箱有改动，不一致！");
                }
                // 方便测试，设置1
                if (userRegisterDTO.getUserVerifyCode() != null && !userRegisterDTO.getUserVerifyCode().equals("")) {
                    String md5Code = MD5Util.encrypt(userRegisterDTO.getUserVerifyCode());
                    if (!md5Code.equals(verifyCode)) {
                        log.info("UserServiceImpl login 输入的验证码错误");
                        return JsonResult.error("输入的验证码错误");
                    }
                    //数字+字母，6-20位. 返回true 否则false
                    boolean isLegal = userRegisterDTO.getPassword().matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,20}$");
                    if (isLegal == false) {
                        log.info("UserServiceImpl login 密码请输入数字+字母，6-20位");
                        return JsonResult.error("密码请输入数字+字母，6-20位");
                    }
                    if (!userRegisterDTO.getPassword().equals(userRegisterDTO.getAgainPassword())) {
                        log.info("UserServiceImpl login 前后密码不对应");
                        return JsonResult.error("前后密码不对应");
                    }
                    userRegisterDTO.setPassword(MD5Util.encrypt(userRegisterDTO.getPassword()));
//                    UserPO user = ConvertMapping.userAO2UserPO(request);
                    UserPO user = new UserPO();
                    BeanUtils.copyProperties(userRegisterDTO,user);

                    int id = userMapper.userAdd(user);
                    String userCode = "user_" + user.getId();
                    user.setUserCode(userCode);
                    userPO = user;
                    log.info("UserServiceImpl login user:[{}]", user);
                    userMapper.updateUserCode(userCode, user.getId());
                } else {
                    log.info("UserServiceImpl login 请输入验证码");
                    return JsonResult.error("请输入验证码");
                }
            } else {
                log.info("UserServiceImpl login 邮箱格式不正确！");
                return JsonResult.error("邮箱格式不正确！");
            }
        }
        // 登录
        else {
            log.info("UserServiceImpl login 该账号注册过了！");
            return JsonResult.error(500, "该账号注册过了！");
        }
        // 生成token，存token进redis
        String token = TokenUtil.createToken(userPO);

        UserTokenVO userTokenVO = new UserTokenVO();
        BeanUtils.copyProperties(userPO, userTokenVO);
        userTokenVO.setToken(token);
        caffeineCache.put(token, userTokenVO);

        log.info("UserServiceImpl login 注册成功！");
        try {
            // 异步通知，同步到ES
            rabbitTemplate.convertAndSend("user.direct","user.register",userPO);
        }catch (Exception e){
            log.error("register 远程通知失败,请检查rabbitmq服务");
        }
        return JsonResult.success(200, "注册成功！");
    }

    /**
     * 通过密码的登录
     */
    @Override
    public JsonResult loginByPassword(UserLoginByPasswordDTO userLoginByPasswordDTO) {
        log.info("UserServiceImpl loginByPassword request:[{}]", userLoginByPasswordDTO);
        // 查询数据库是否存在该用户（有则直接登录，并生成token）
        UserPO userPO = userMapper.getUserByEmail(userLoginByPasswordDTO.getEmail());
        if (Objects.isNull(userPO)) {
            log.info("UserServiceImpl loginByPassword email:[{}] 该账号还没注册过，请先注册！", userLoginByPasswordDTO.getEmail());
            return JsonResult.error("该账号还没注册过，请先注册！");
        }
        // 登录
        else {
            String md5Password = MD5Util.encrypt(userLoginByPasswordDTO.getPassword());
            if (!md5Password.equals(userPO.getPassword())) {
                log.info("UserServiceImpl loginByPassword 密码错误！");
                return JsonResult.error("密码错误！");
            }
        }
        // 生成token，存token进redis
        String token = TokenUtil.createToken(userPO);
//        UserLoginReqVO userLoginReqVO = ConvertMapping.userPO2UserLoginReqVO(userPO);
        UserTokenVO userTokenVO = new UserTokenVO();
        BeanUtils.copyProperties(userPO, userTokenVO);
        userTokenVO.setToken(token);
        caffeineCache.put(token, userTokenVO);

        return JsonResult.success(userTokenVO);
    }


/**
     * 通过验证码登录 或 注册
     */

    @Override
    public JsonResult loginByVerifyCode(UserAO request) {
        log.info("UserServiceImpl loginByVerifyCode request:[{}]", request);
        Boolean emailIsLegal = (request.getEmail() != null && request.getEmail().matches("^[1-9][0-9]{4,10}@qq\\.com$"));
        if (emailIsLegal == false) {
            log.info("UserServiceImpl loginByVerifyCode 邮箱格式不正确！");
            return JsonResult.error("邮箱格式不正确");
        }
        // 注册
            Map<String,String> cacheInfo = (Map<String,String>) caffeineCache.getIfPresent(request.getEmail());
            if(cacheInfo==null){
                log.info("UserServiceImpl register 缓存中没有验证码数据，请先发送验证码");
                return JsonResult.error(500,"请发送验证码");
            }
            String verifyCode = cacheInfo.get("verifyCode");
            String PreEmail = cacheInfo.get("preEmail");

        if (request.getUserVerifyCode() == null || "".equals(request.getUserVerifyCode())) {
            return JsonResult.error(500, "验证码错误");
        }
        if (!PreEmail.equals(MD5Util.encrypt(request.getEmail()))) {
            log.info("UserServiceImpl loginByVerifyCode 邮箱有改动，不一致！");
            return JsonResult.error("邮箱有改动，不一致！");
        }
        // 查询数据库是否存在该用户（有则直接登录，并生成token）
        String md5Code = MD5Util.encrypt(request.getUserVerifyCode());
        if (!md5Code.equals(verifyCode)) {
            log.info("UserServiceImpl loginByVerifyCode 验证码错误！");
            return JsonResult.error(500, "验证码错误！");
        }
        UserPO userPO = userMapper.getUserByEmail(request.getEmail());
        // 如果没有注册过，则注册
        if (Objects.isNull(userPO)) {
            request.setPassword(MD5Util.encrypt("ljh2003"));
            request.setUsername(request.getEmail().split("@")[0]);
            UserPO user = new UserPO();
            BeanUtils.copyProperties(request,user);
            int id = userMapper.userAdd(user);
            String userCode = "user_" + user.getId();
            user.setUserCode(userCode);
            userPO = user;
            log.info("UserServiceImpl loginByVerifyCode user:[{}]", user);
            userMapper.updateUserCode(userCode, user.getId());
        }
        // 生成token，存token进redis
        String token = TokenUtil.createToken(userPO);
        UserTokenVO userTokenVO = new UserTokenVO();
        BeanUtils.copyProperties(userPO, userTokenVO);
        userTokenVO.setToken(token);
        caffeineCache.put(token, userTokenVO);

        return JsonResult.success(userTokenVO);
    }

    @Override
    public JsonResult passwordForget(UserForgetPasswordDTO userForgetPasswordDTO) {
        log.info("UserServiceImpl passwordForget userForgetPasswordDTO:[{}]", userForgetPasswordDTO);
        UserPO userPO = userMapper.getUserByEmail(userForgetPasswordDTO.getEmail());
        Boolean emailIsLegal = (userForgetPasswordDTO.getEmail() != null && userForgetPasswordDTO.getEmail().matches("^[1-9][0-9]{4,10}@qq\\.com$"));
        if (emailIsLegal == false) {
            log.info("UserServiceImpl passwordForget 邮箱格式不正确！");
            return JsonResult.error(500, "邮箱格式不正确");
        }
        Map<String,String> cacheInfo = (Map<String,String>) caffeineCache.getIfPresent(userPO.getEmail());
        if(cacheInfo==null){
            log.info("UserServiceImpl passwordForget 缓存中没有验证码数据，请先发送验证码");
            return JsonResult.error(500,"请发送验证码");
        }
        String verifyCode = cacheInfo.get("verifyCode");
        String PreEmail = cacheInfo.get("preEmail");

        if (verifyCode== null || "".equals(verifyCode)) {
            return JsonResult.error(500, "验证码错误");
        }
        if (!PreEmail.equals(MD5Util.encrypt(userForgetPasswordDTO.getEmail()))) {
            log.info("UserServiceImpl passwordForget 邮箱有改动，不一致！");
            return JsonResult.error(500, "邮箱有改动，不一致！");
        }
        if (Objects.isNull(userPO)) {
            log.info("UserServiceImpl passwordForget email:[{}] 该账号还没注册过，请先注册！", userForgetPasswordDTO.getEmail());

            return JsonResult.error(500, "该账号还没注册过，请先注册！");
        }
        if (userForgetPasswordDTO.getUserVerifyCode() != null && !userForgetPasswordDTO.getUserVerifyCode().equals("")) {
            String md5Code = MD5Util.encrypt(userForgetPasswordDTO.getUserVerifyCode());

            if (!md5Code.equals(verifyCode)) {
                log.info("UserServiceImpl passwordForget 输入的验证码错误");
                return JsonResult.error(500, "输入的验证码错误");
            }
            //数字+字母，6-20位. 返回true 否则false
            boolean isLegal = userForgetPasswordDTO.getPassword().matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,20}$");
            if (isLegal == false) {
                log.info("UserServiceImpl passwordForget 密码请输入数字+字母，6-20位");
                return JsonResult.error(500, "密码请输入数字+字母，6-20位");
            }
            if (!userForgetPasswordDTO.getPassword().equals(userForgetPasswordDTO.getAgainPassword())) {
                log.info("UserServiceImpl passwordForget 前后密码不对应");
                return JsonResult.error(500, "前后密码不对应");
            }
            String newPassword = MD5Util.encrypt(userForgetPasswordDTO.getPassword());
            userMapper.updatePassword(newPassword, userForgetPasswordDTO.getEmail());
        }

        return JsonResult.success(200, "密码重置成功！");
    }

    @Override
    public JsonResult loginByScan(String pid, String did, String createTime) {
        log.info("UserServiceImpl loginByScan pid:[{}], did:[{}], createTime:[{}]", pid, did, createTime);
        Map<String, Boolean> map = new HashMap<>();
        // 锁定以防止并发问题
        synchronized (lock) {
            if (StorageUtils.scanLoginConflictMap.contains(createTime)) {
                map.put("loginStatus", false);// 检测到重复扫码，直接返回登录失败

                return JsonResult.success(map);
            }
            StorageUtils.scanLoginConflictMap.add(createTime);// 记录createTime，防止冲突
            // 查询数据库是否存在该用户
            UserPO havaUser = userMapper.getUserByEmail(did);
            // 如果没有注册过，则注册
            if (Objects.isNull(havaUser)) {
                UserPO user = new UserPO();
                user.setEmail(did);
                user.setUsername(did);
                int id = userMapper.userAdd(user);
                String userCode = "user_" + user.getId();
                user.setUserCode(userCode);
                havaUser = user;
                log.info("UserServiceImpl loginByScan user:[{}]", user);
                userMapper.updateUserCode(userCode, user.getId());
            }

            // 用户存在或注册完成，生成 token
            String token = TokenUtil.createToken(havaUser);
            UserTokenVO userTokenVO = new UserTokenVO();
            BeanUtils.copyProperties(havaUser, userTokenVO);
            userTokenVO.setToken(token);
            // 缓存 token 和登录状态
            caffeineCache.put(token, userTokenVO);
            StorageUtils.loginMap.put(createTime, JsonResult.success(userTokenVO));
//            StorageUtils.scanLoginConflictMap.add(createTime);
            map.put("loginStatus", true);

            return JsonResult.success(map);
        }
    }

    @Override
    public JsonResult loginByScanListen(String pid, String createTime) {
        log.info("UserServiceImpl loginByScanListen pid:[{}], createTime:[{}]", pid, createTime);
        JsonResult response = null;
        int timeCount = 0;
        // 开启轮询等待登录结果
        while (true) {
            System.out.println("---------->" + StorageUtils.loginMap);
            // 监听pid的登录
            if (StorageUtils.loginMap.containsKey(createTime)) {
                System.out.println();
                // 如果找到了匹配的登录信息，则返回结果并移除
                response = StorageUtils.loginMap.get(createTime);
                StorageUtils.loginMap.remove(createTime);
                log.info("UserServiceImpl loginByScanListen --> 去除结果 -> response:[{}]", response);

                return response;
            }
            // 没有找到登录信息，则继续等待
            try {
                Thread.sleep(200);
                timeCount += 200;
            } catch (InterruptedException e) {
                throw new RuntimeException();
            }
            // 超时检查，2分钟内未登录则结束监听
            if (timeCount > 120000) {
                break;
            }
        }

        return response;
    }

    @Override
    public JsonResult userPasswordUpdate(String userCode, String oldPassword, String newPassword, String confirmPassword) {
        log.info("UserServiceImpl userPasswordUpdate userCode:[{}], oldPassword:[{}], newPassword:[{}], confirmPassword:[{}]", userCode, oldPassword, newPassword, confirmPassword);
        if (userCode == null || "".equals(userCode)) {
            log.info("UserServiceImpl userPasswordUpdate 请先登录");
            return JsonResult.error(401, "请先登录");
        }
        // 判断输入的原密码是否正确
        UserPO userPO = userMapper.getUserByCode(userCode);
        if (!MD5Util.encrypt(oldPassword).equals(userPO.getPassword())) {
            log.info("UserServiceImpl userPasswordUpdate 原密码输入错误");
            return JsonResult.error(500, "原密码输入错误");
        }
        // 数字+字母，6-20位. 返回true 否则false
        boolean isLegal = newPassword.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,20}$");
        if (isLegal == false) {
            log.info("UserServiceImpl userPasswordUpdate 密码请输入数字+字母，6-20位");
            return JsonResult.error("密码请输入数字+字母，6-20位");
        }
        if (!newPassword.equals(confirmPassword)) {
            log.info("UserServiceImpl userPasswordUpdate 两次密码不一致");
            return JsonResult.error(500, "两次密码不一致");
        }
        String password = MD5Util.encrypt(newPassword);
        log.info("UserServiceImpl userPasswordUpdate md5Password:[{}]", password);
        userMapper.userPasswordUpdate(userCode, password);

        return JsonResult.success("修改成功");
    }

    @Override
    public JsonResult sendEmailVerifyCode(String email) {
        log.info("UserServiceImpl sendEmailVerifyCode email:[{}]", email);
        // 判断邮箱合法性
        Boolean emailIsLegal = (email != null && email.matches("^[1-9][0-9]{4,10}@qq\\.com$"));
        if (emailIsLegal == false) {
            log.info("UserServiceImpl sendEmailVerifyCode 邮箱格式不正确！");
            return JsonResult.error("邮箱格式不正确");
        }
        // 发邮件
        // 生成4位验证码
        String verifyCode = MailUtil.createVerifyCode();
        log.info("UserServiceImpl sendEmailVerifyCode verifyCode:[{}]", verifyCode);
        try {
            // 发送邮件
            MailUtil.sendSimpleMail("学生校园助手注册验证码", email, verifyCode);
        } catch (Exception e) {
            return JsonResult.error(500, "发送邮件失败！");
        }


        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("preEmail", MD5Util.encrypt(email));
        responseMap.put("verifyCode", MD5Util.encrypt(verifyCode));
        log.info("UserServiceImpl sendEmailVerifyCode responseMap:[{}]", responseMap);
        // 存入缓存
        caffeineCache.put(email,responseMap);

        return JsonResult.success(200,"发送成功");
    }




    @Override
    public String getUserCodeByToken(String token) {
        String userCode = null;

        if (token != null && !"".equals(token)) {
            UserTokenVO userTokenVO = TokenUtil.getUserFromToken(token);

            userCode = userTokenVO.getUserCode();
        }

        return userCode;
    }


    @Override
    public JsonResult userInfo(String userCode) {
        log.info("UserServiceImpl userInfo userCode:[{}]", userCode);
        if (userCode == null) {
            return JsonResult.error(401, "token失效或过期");
        }
        UserPO userPO = userMapper.getUserByCode(userCode);

        return JsonResult.success(userPO);
    }

    @Override
    public JsonResult userInfoUpdate(String token, UserUpdateRequestAO request) {
        log.info("UserServiceImpl userInfoUpdate token:[{}], request:[{}]", token, request);
        if (token == null || "".equals(token)) {
            return JsonResult.error(401, "请先登录");
        }
        String userCode = getUserCodeByToken(token);
        if (userCode == null) {
            return JsonResult.error(401, "token失效或过期");
        }
        UserAO userAO = new UserAO();
        UserPO userPO = userMapper.getUserByCode(userCode);

        // 没有minio设置默认
        userAO.setHeadshot("https://tse4-mm.cn.bing.net/th/id/OIP-C.pL9aeO50HMujMSzGcOPhKwAAAA?rs=1&pid=ImgDetMain");
/*        if (Objects.isNull(request.getheadshot())) {
            userAO.setheadshot(userPO.getheadshot());
        } else {
            UploadResponse uploadResponse = minioUtil.uploadFile(request.getheadshot(), "file");
            userAO.setheadshot(uploadResponse.getMinIoUrl());
        }*/

        if (request.getUsername()!=null && request.getUsername().length() > 50) {
            log.info("UserServiceImpl userInfoUpdate 用户名长度最大为50");
            return JsonResult.error("用户名长度最大为50");
        }
        userAO.setUserCode(userCode);
        userAO.setUsername(request.getUsername());
        userAO.setSchool(request.getSchool());
        userAO.setStudentNum(request.getStudentNum());
        userMapper.userInfoUpdate(userAO);
        userPO = userMapper.getUserByCode(userCode);


        try {
            // 异步通知，同步到ES
            rabbitTemplate.convertAndSend("user.direct","user.update",userPO);
        }catch (Exception e){
            log.error("userInfoUpdate 远程通知失败,请检查rabbitmq服务");
        }
        return JsonResult.success(userPO);
    }

}
