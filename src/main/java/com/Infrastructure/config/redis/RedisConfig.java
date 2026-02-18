package com.infrastructure.config.redis;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import org.springframework.context.annotation.PropertySource;
import redis.embedded.RedisServer;

import java.io.IOException;

@Configuration
@PropertySource("classpath:hibernate-redis.properties")
public class RedisConfig {

    @Value("${redis.host:localhost}")
    private String redisHost;

    @Value("${redis.port:6379}")
    private int redisPort;

    @Value("${redis.database:0}")
    private int redisDatabase;

    @Bean(destroyMethod = "shutdown")
    @Profile("!dev")
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://" + redisHost + ":" + redisPort)
                .setDatabase(redisDatabase);
        return Redisson.create(config);
    }

    @Bean(destroyMethod = "stop")
    @Profile("dev")
    public RedissonClient embeddedRedissonClient(RedisServer embeddedRedis) {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://localhost:" + embeddedRedis.ports())
                .setDatabase(redisDatabase);
        return Redisson.create(config);
    }

    @Bean(destroyMethod = "stop")
    @Profile("dev")
    public RedisServer embeddedRedis() throws IOException {
        RedisServer redisServer = RedisServer.builder()
                .port(redisPort)
                .build();
        redisServer.start();
        return redisServer;
    }
}
