package com.laojiahuo.ictproject.config.websocket;

import com.alibaba.fastjson.JSONObject;
import com.laojiahuo.ictproject.MachineTranslationMain;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket 跨文化聊天室服务
 */
@ServerEndpoint(value = "/imserver/{username}/{from}")
@Component
public class WebSocketServer {

    @Data
    private static class UserInfo {
        private String username;
        private String from;
    }

    private static final Logger log = LoggerFactory.getLogger(WebSocketServer.class);

    // 存储在线用户的 WebSocket 会话
    private static final Map<UserInfo, Session> sessionMap = new ConcurrentHashMap<>();

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username, @PathParam("from") String from) {
        UserInfo userInfo = new UserInfo();
        userInfo.setUsername(username);
        userInfo.setFrom(from);
        sessionMap.put(userInfo, session);

        log.info("来自 {} 用户加入，username={}, 当前在线人数为：{}", from, username, sessionMap.size());
        sendAllMessage("欢迎来自 [" + from + "] 的用户: " + username + " 加入跨文化交流聊天室！");
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session, @PathParam("username") String username, @PathParam("from") String from) {
        // 找到并移除该用户
        sessionMap.entrySet().removeIf(entry -> entry.getKey().getUsername().equals(username) && entry.getKey().getFrom().equals(from));

        log.info("用户离开，username={}, 当前在线人数为：{}", username, sessionMap.size());
        sendAllMessage(username + " 离开了聊天室！");
    }

    /**
     * 收到客户端消息后调用的方法
     */
    @OnMessage
    public void onMessage(String message, Session session, @PathParam("username") String username, @PathParam("from") String from) {
        log.info("服务端收到用户 username={} 的消息: {}", username, message);

        // 从 sessionMap 查找发送消息的用户信息
        UserInfo senderInfo = sessionMap.keySet().stream()
                .filter(userInfo -> userInfo.getUsername().equals(username) && userInfo.getFrom().equals(from))
                .findFirst().orElse(null);

        if (senderInfo != null) {
            sendAllMessage(message, senderInfo);
        }
    }

    /**
     * 发生错误调用的方法
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误: {}", error.getMessage());
        error.printStackTrace();
    }

    /**
     * 服务端发送消息给指定客户端
     */
    private void sendMessage(String message, Session toSession) {
        try {
            if (toSession.isOpen()) {
                toSession.getBasicRemote().sendText(message);
            }
        } catch (Exception e) {
            log.error("发送消息失败: {}", e.getMessage());
        }
    }

    /**
     * 服务端发送消息给所有客户端
     */
    private void sendAllMessage(String message) {
        sessionMap.values().forEach(session -> sendMessage(message, session));
    }

    /**
     * 服务端发送消息给所有客户端，并根据语言进行翻译
     *
     * @param message 原始消息
     * @param senderInfo 发送消息的用户信息
     */
    private void sendAllMessage(String message, UserInfo senderInfo) {
        String senderLanguage = senderInfo.getFrom();

        for (Map.Entry<UserInfo, Session> entry : sessionMap.entrySet()) {
            UserInfo recipientInfo = entry.getKey();
            Session recipientSession = entry.getValue();
            String recipientLanguage = recipientInfo.getFrom();

            String translatedMessage = message;
            if (!senderLanguage.equals(recipientLanguage)) {
                // 只有当发送者与接收者语言不同时，才进行翻译
                JSONObject translationResult = MachineTranslationMain.translate(message, senderLanguage, recipientLanguage);
                JSONObject transResult = translationResult.getJSONObject("trans_result");
                String dst = transResult.getString("dst");
                String src = transResult.getString("src");
                translatedMessage = dst + " (原文: " + src + ")";
            }

            sendMessage(senderInfo.getUsername() + ": " + translatedMessage, recipientSession);
        }
    }
}
