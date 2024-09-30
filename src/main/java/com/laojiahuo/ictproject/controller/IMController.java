package com.laojiahuo.ictproject.controller;

import com.laojiahuo.ictproject.utils.JsonResult;
import io.swagger.annotations.ApiModel;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@ApiModel("聊天室")
@RestController
@Slf4j
public class IMController {
    /**
     * 用户进入群聊
     * 1.查询我的群组
     * 2.查询我的好友
     */
    @PostMapping("/group")
    public JsonResult groupEntry(HttpServletRequest httpServletRequest){
        // 这边先解析用户信息,先清除缓存,再kv放缓存里,让别人去拿
        return JsonResult.success("暂未实现");
    }

}
