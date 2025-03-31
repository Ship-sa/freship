package me.yeon.freship.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.core.script.RedisScript;

@Configuration
public class RedisLockConfig {

    @Bean
    public RedisScript<Boolean> checkAndReleaseLockScript() {
        Resource scriptResource = new ClassPathResource("scripts/checkAndReleaseLock.lua");
        return RedisScript.of(scriptResource, Boolean.class);
    }
}
