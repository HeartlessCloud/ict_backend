package com.laojiahuo.ictproject.service.impl;

import com.laojiahuo.ictproject.DTO.QuestionDTO;
import com.laojiahuo.ictproject.config.CustomException;
import com.laojiahuo.ictproject.constants.GPTConstants;
import com.laojiahuo.ictproject.PO.MessagePO;
import com.laojiahuo.ictproject.service.BigModelService;
import com.laojiahuo.ictproject.service.MessageService;
import com.laojiahuo.ictproject.utils.SseEmitterCache;
import com.laojiahuo.ictproject.utils.BigModelNew;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

import static com.laojiahuo.ictproject.utils.BigModelNew.getAuthUrl;

@Slf4j
@Service
public class BigModelServiceImpl implements BigModelService {
    @Autowired
    private MessageService messageService;


    @Override
    public SseEmitter getResult(String userCode, QuestionDTO questionDTO) {
        log.info("BigModelServiceImpl getResult userCode:[{}], questionDTO:[{}]", userCode, questionDTO);
//        if (userCode == null || "".equals(userCode)) {
//            log.info("BigModelServiceImpl getResult 用户未登录");
///*            throw new CustomException(500, "用户未登录");*/
//            userCode = "user_-1";
//        }
        if (questionDTO.getContent() == null || "".equals(questionDTO.getContent())) {
            log.info("BigModelServiceImpl getResult 内容为空");
            throw new CustomException(500, "请先输入文字描述");
        }

        SseEmitter sseEmitter = new SseEmitter(SseEmitterCache.TIMEOUT);
        SseEmitterCache.addEmitter(userCode,sseEmitter);
        // 查出历史数据
        List<MessagePO> historyList=messageService.findMessage(userCode);
        String authUrl = null;
        try {
            authUrl = getAuthUrl(GPTConstants.XF_XH_HOST_URL, GPTConstants.XF_XH_API_KEY,GPTConstants.XF_XH_API_SECRET_KEY);
        } catch (Exception e) {
            throw new CustomException(500, "鉴权失败,请查看api参数");
        }
        OkHttpClient client = new OkHttpClient.Builder().build();
        String url = authUrl.toString().replace("http://", "ws://").replace("https://", "wss://");
        Request request = new Request.Builder().url(url).build();
        // 创建大模型
        BigModelNew bigModel = new BigModelNew(userCode, false, sseEmitter, questionDTO.getContent(),historyList,messageService);

        WebSocket webSocket = client.newWebSocket(request, bigModel);


        return sseEmitter;


    }
}
