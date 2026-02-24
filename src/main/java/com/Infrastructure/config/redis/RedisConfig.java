package com.infrastructure.config.redis;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.embedded.RedisServer;

import java.io.IOException;

@Configuration
@PropertySource("classpath:hibernate-redis.properties")
public class RedisConfig {

    @Value("${redis.host}")
    private String redisHost;

    @Value("${redis.port:6379}")
    private int redisPort;

    @Value("${redis.database:0}")
    private int redisDatabase;

    @Value("${redis.connectionPoolSize}")
    private int connectionPoolSize;

    @Value("${redis.connectionMinimumIdleSize}")
    private int connectionMinimumIdleSize;

    @Value("${redis.connectTimeout}")
    private int connectTimeout;

    @Value("${redis.retryAttempts}")
    private int retryAttempts;

    @Value("${redis.retryInterval}")
    private int retryInterval;

    @Bean(destroyMethod = "shutdown")
    @Profile("!dev")
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://" + redisHost + ":" + redisPort)
                .setDatabase(redisDatabase);
        return Redisson.create(config);
    }

    @Bean(destroyMethod = "shutdown")
    @Profile("dev")
    @DependsOn("embeddedRedis")
    public RedissonClient embeddedRedissonClient(RedisServer embeddedRedis) throws Exception {
        Thread.sleep(1000);

        Config config = new Config();
        int port = embeddedRedis.ports().get(0);
        config.useSingleServer()
                .setAddress("redis://" + redisHost + ":" + port)
                .setDatabase(redisDatabase)
                .setConnectionPoolSize(connectionPoolSize)
                .setConnectionMinimumIdleSize(connectionMinimumIdleSize)
                .setConnectTimeout(connectTimeout)
                .setRetryAttempts(retryAttempts)
                .setRetryInterval(retryInterval);

        return Redisson.create(config);
    }

    @Bean(destroyMethod = "stop")
    @Profile("dev")
    public RedisServer embeddedRedis() throws IOException {
        RedisServer redisServer = new RedisServer(redisPort);
        redisServer.start();
        return redisServer;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedissonClient redissonClient) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(new RedissonConnectionFactory(redissonClient));
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }
}
