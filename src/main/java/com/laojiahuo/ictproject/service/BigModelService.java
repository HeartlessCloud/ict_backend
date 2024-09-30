package com.laojiahuo.ictproject.service;

import com.laojiahuo.ictproject.DTO.QuestionDTO;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface BigModelService {
    SseEmitter getResult(String userCode, QuestionDTO questionDTO);
}
