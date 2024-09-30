package com.laojiahuo.ictproject.utils;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 管理sseEmitter对象
 */
public class SseEmitterCache {

    // 超时时间设置
    public static final long TIMEOUT = 300000L;

    // 使用 ConcurrentHashMap 保存 SseEmitter 实例
    private static final Map<String, SseEmitter> sseCache = new ConcurrentHashMap<>();

    // 添加 SseEmitter 实例到缓存
    public static void addEmitter(String userId, SseEmitter emitter) {
        sseCache.put(userId, emitter);
    }

    // 根据用户ID获取 SseEmitter 实例
    public static SseEmitter getEmitter(String userId) {
        return sseCache.get(userId);
    }

    // 移除指定用户ID的 SseEmitter 实例
    public static void removeEmitter(String userId) {
        sseCache.remove(userId);
    }

    // 获取所有 SseEmitter 实例的集合
    public static Map<String, SseEmitter> getAllEmitters() {
        return sseCache;
    }
}
