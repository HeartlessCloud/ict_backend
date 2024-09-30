package com.laojiahuo.ictproject.redis;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

@Component
public class RedisComponent {
    @Resource
    private RedisUtils redisUtils;

    public Long getUserHeartBeat(String userCode){
        return (Long) redisUtils.get("user:heart:beat"+userCode);
    }

    public boolean saveHeartBeat(String userCode){
        return redisUtils.set("user:heart:beat"+userCode,System.currentTimeMillis(),30);
    }

    public void removeUserHeartBeat(String userCode) {
        redisUtils.del("user:heart:beat"+userCode);
    }
}
