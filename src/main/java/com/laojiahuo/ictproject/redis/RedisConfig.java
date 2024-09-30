package com.laojiahuo.ictproject.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisConfig<V> {

    @Bean("redisTemplate")
    public <V> RedisTemplate<String, V> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, V> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // 设置key的序列化方式为String
        template.setKeySerializer(RedisSerializer.string());

        // 设置value的序列化方式为JSON
        template.setValueSerializer(RedisSerializer.json());

        // 设置hash的key的序列化方式为String
        template.setHashKeySerializer(RedisSerializer.string());

        // 设置hash的value的序列化方式为JSON
        template.setHashValueSerializer(RedisSerializer.json());

        // 初始化RedisTemplate的属性
        template.afterPropertiesSet();

        return template;
    }
}
