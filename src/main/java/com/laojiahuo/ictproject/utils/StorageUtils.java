package com.laojiahuo.ictproject.utils;

import cn.hutool.core.collection.ConcurrentHashSet;

import java.util.concurrent.ConcurrentHashMap;


public class StorageUtils {

    /**
     * 存储【监听扫码登录状态】的标识
     */
    public static ConcurrentHashMap<String, JsonResult> loginMap = new ConcurrentHashMap<>();

    /**
     * 存储【扫码冲突】的标识
     */
    public static ConcurrentHashSet<String> scanLoginConflictMap = new ConcurrentHashSet<>();

    /**
     * 存储【监听停止请求】的标识
     */
    public static ConcurrentHashMap<String, String> stopRequestMap = new ConcurrentHashMap<>();

    /**
     * ppt大纲query
     */
    public static ConcurrentHashMap<String, String> outlineQueryMap = new ConcurrentHashMap<>();

}
