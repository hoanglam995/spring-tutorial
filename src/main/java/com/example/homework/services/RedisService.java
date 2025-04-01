package com.example.homework.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, Object> redisTemplate;

    // Lưu dữ liệu vào Redis
    public void save(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }
    public void save(String key, Object value, long timeout) {
        redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
    }
    public void save(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    // Lấy dữ liệu từ Redis
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    // Lấy thời gian sống còn lại (giây)
    public Long getTTL(String key) {
        return redisTemplate.getExpire(key);
    }

    // Xóa dữ liệu
    public void delete(String key) {
        redisTemplate.delete(key);
    }
}
