package com.rain.flame.starter.config.autoconfiguration;

import com.rain.flame.config.spring.beans.factory.annatation.RedisRegistryBeanPostProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class RegistryAutoConfiguration {
    @Configuration
    @Import(RedisRegistryBeanPostProcessor.class)
    class RedisRegistryAutoConfiguration {
    }
}
