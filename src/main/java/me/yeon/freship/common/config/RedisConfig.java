package me.yeon.freship.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean // RedisTemplate<String, Object>를 빈으로 등록하여 애플리케이션에서 사용할 수 있게 함
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        // RedisConnectionFactory는 Redis 서버와의 연결을 관리하는 객체로, 이를 사용해 RedisTemplate의 연결을 설정
        template.setConnectionFactory(redisConnectionFactory);

        template.setKeySerializer(new StringRedisSerializer()); // key 값을 String으로 저장
        template.setValueSerializer(new GenericToStringSerializer<>(String.class)); // value 값을 String으로 저장
        return template;
    }

}
