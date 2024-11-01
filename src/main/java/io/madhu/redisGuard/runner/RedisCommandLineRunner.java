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
        redisService.saveValue("firstName", "madhu");
        redisService.saveValue("lastName", "g");
        redisService.saveValue("fullname", "g madhukar");

        log.info("The value {}", redisService.getValue("name"));
        log.info("The value {}", redisService.getValue("firstName"));
        log.info("The value {}", redisService.getValue("lastName"));
        log.info("The value {}", redisService.getValue("fullname"));

        // redisService.saveValue("firstName", "madhu");

        redisService.saveValue("COUNTRY", "SINGAPORE");
        log.info("The value {}", redisService.getValue("COUNTRY"));

        redisService.saveValue("string-1", "SINGAPORE");
        redisService.saveValue("string-2", "india");
    }
}
