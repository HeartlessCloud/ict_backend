//package com.laojiahuo.ictproject.config.websocket;
//
//import com.alibaba.fastjson.JSONObject;
//import com.laojiahuo.ictproject.MachineTranslationMain;
//import jakarta.websocket.*;
//import jakarta.websocket.server.PathParam;
//import jakarta.websocket.server.ServerEndpoint;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Component;
//
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
///**
// * WebSocket 聊天室服务
// */
//@ServerEndpoint(value = "/imserver/{username}/{from}/{to}")
//@Component
//public class WebSocketServerBak {
//    private static final Logger log = LoggerFactory.getLogger(WebSocketServerBak.class);
//
//    // 存储在线用户的 WebSocket 会话
//    private static final Map<String, Session> sessionMap = new ConcurrentHashMap<>();
//
//    /**
//     * 连接建立成功调用的方法
//     */
//    @OnOpen
//    public void onOpen(Session session, @PathParam("username") String username) {
//        // 发送欢迎消息，包括文化交流的主题
//        sessionMap.put(username, session);
//        log.info("用户加入，username={}, 当前在线人数为：{}", username, sessionMap.size());
//        sendAllMessage("欢迎 " + username + " 加入跨文化交流聊天室！请分享您的文化故事和观点。");
//    }
//
//    /**
//     * 连接关闭调用的方法
//     */
//    @OnClose
//    public void onClose(Session session, @PathParam("username") String username) {
//        sessionMap.remove(username);
//        log.info("用户离开，username={}, 当前在线人数为：{}", username, sessionMap.size());
//        sendAllMessage(username + " 离开了聊天室！");
//    }
//
//    /**
//     * 收到客户端消息后调用的方法
//     */
//    @OnMessage
//    public void onMessage(String message, Session session,
//                          @PathParam("username") String username,
//                          @PathParam("from") String from,
//                          @PathParam("to") String to) {
//        log.info("服务端收到用户username={}的消息: {}", username, message);
////        JSONObject translate = MachineTranslationMain.translate(message,"cn","en");
//        JSONObject translate = MachineTranslationMain.translate(message,from,to);
//        JSONObject transResult = translate.getJSONObject("trans_result");
//        String dst = transResult.getString("dst");
//        String src = transResult.getString("src");
//        sendAllMessage(username + ": " + "[From]"+src+ "[To]"+dst);
//    }
//
//    /**
//     * 发生错误调用的方法
//     */
//    @OnError
//    public void onError(Session session, Throwable error) {
//        log.error("发生错误: {}", error.getMessage());
//        error.printStackTrace();
//    }
//
//    /**
//     * 服务端发送消息给指定客户端
//     */
//    private void sendMessage(String message, Session toSession) {
//        try {
//            if (toSession.isOpen()) {
//                toSession.getBasicRemote().sendText(message);
//            }
//        } catch (Exception e) {
//            log.error("发送消息失败: {}", e.getMessage());
//        }
//    }
//
//    /**
//     * 服务端发送消息给所有客户端
//     */
//    private void sendAllMessage(String message) {
//        for (Session session : sessionMap.values()) {
//            sendMessage(message, session);
//        }
//    }
//}
