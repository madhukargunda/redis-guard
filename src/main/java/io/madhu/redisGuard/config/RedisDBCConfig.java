/**
 * Author: Madhu
 * User:madhu
 * Date:30/10/24
 * Time:4:34 PM
 * Project: redis-guard
 */

package io.madhu.redisGuard.config;

import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

//@Configuration
public class RedisDBCConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }
}
