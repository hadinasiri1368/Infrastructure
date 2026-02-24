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

    @Bean(destroyMethod = "shutdown")
    @Profile("dev")
    @DependsOn("embeddedRedis")
    public RedissonClient embeddedRedissonClient(RedisServer embeddedRedis) throws Exception {
        // صبر کنید تا Redis کاملاً آماده شود
        Thread.sleep(1000);

        Config config = new Config();
        int port = embeddedRedis.ports().get(0);
        System.out.println("✅ Embedded Redis started on port: " + port);

        config.useSingleServer()
                .setAddress("redis://localhost:" + port)
                .setDatabase(redisDatabase)
                .setConnectionPoolSize(10)
                .setConnectionMinimumIdleSize(5)
                .setConnectTimeout(10000)
                .setRetryAttempts(3)
                .setRetryInterval(1500);

        return Redisson.create(config);
    }

    @Bean(destroyMethod = "stop")
    @Profile("dev")
    public RedisServer embeddedRedis() throws IOException {
        try {
            RedisServer redisServer = new RedisServer(6379);
            redisServer.start();
            System.out.println("✅ Embedded Redis started successfully");
            return redisServer;
        } catch (Exception e) {
            System.err.println("❌ Failed to start Redis on port 6379: " + e.getMessage());
            // اگر پورت 6379 گرفته بود، از پورت دیگری استفاده کنید
            RedisServer redisServer = new RedisServer(6380);
            redisServer.start();
            System.out.println("✅ Embedded Redis started on port: 6380");
            return redisServer;
        }
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
