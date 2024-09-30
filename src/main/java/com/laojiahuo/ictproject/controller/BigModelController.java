package com.laojiahuo.ictproject.controller;

import com.laojiahuo.ictproject.DTO.QuestionDTO;
import com.laojiahuo.ictproject.service.BigModelService;
import com.laojiahuo.ictproject.service.UserService;
import com.laojiahuo.ictproject.utils.JsonResult;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@ApiModel("讯飞星火")
@Slf4j
@RestController
public class BigModelController {
    @Autowired
    private UserService userService;
    @Autowired
    private BigModelService bigModelService;
    @ApiOperation("大模型问答")
    @PostMapping("/agent/question")
    public SseEmitter  question(HttpServletRequest httpServletRequest,
                               @RequestBody QuestionDTO questionDTO){
        String token = httpServletRequest.getHeader("Authorization");
//        String userCode = userService.getUserCodeByToken(token);
        String userCode = "user_1";
        log.info("ChatGptController xfQuestion userCode:[{}], questionDTO:[{}]", userCode, questionDTO);

        SseEmitter response = bigModelService.getResult(userCode,questionDTO);

        return response;
    }

}
