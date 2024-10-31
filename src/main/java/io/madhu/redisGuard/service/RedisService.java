/**
 * Author: Madhu
 * User:madhu
 * Date:30/10/24
 * Time:12:15 AM
 * Project: redis-guard
 */

package io.madhu.redisGuard.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisService {

    @Autowired
    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public RedisService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveValue(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public String getValue(String key) {
        return (String) redisTemplate.opsForValue().get(key);
    }
}
