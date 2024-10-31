/**
 * Author: Madhu
 * User:madhu
 * Date:30/10/24
 * Time:10:30 AM
 * Project: redis-guard
 */

package io.madhu.redisGuard.runner;

import io.madhu.redisGuard.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RedisCommandLineRunner implements CommandLineRunner {

    @Autowired
    RedisService redisService;

    @Override
    public void run(String... args) throws Exception {
        redisService.saveValue("name", "madhu");
        log.info("The value {}", redisService.getValue("name"));
    }
}
