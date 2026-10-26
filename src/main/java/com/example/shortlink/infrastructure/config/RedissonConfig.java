package com.example.shortlink.infrastructure.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "redis.enabled", havingValue = "true", matchIfMissing = true)
public class RedissonConfig {

    @Value("${redis.host}")
    private String redisHost;

    @Value("${redis.password}")
    private String redisPassword;

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient() {
        Config config = new Config();

        config.useSingleServer()
                // Cấu hình các thông số giống hệt file YAML cũ của bạn
                .setAddress(redisHost)
                .setPassword(redisPassword)
                .setIdleConnectionTimeout(10000)
                .setConnectTimeout(10000)
                .setTimeout(3000)
                .setRetryAttempts(3)
                .setRetryInterval(1500)
                .setSubscriptionsPerConnection(5)
                .setSubscriptionConnectionMinimumIdleSize(1)
                .setSubscriptionConnectionPoolSize(50)
                .setConnectionMinimumIdleSize(10)
                .setConnectionPoolSize(64)
                .setDatabase(0)
                .setDnsMonitoringInterval(5000);

        // Set Codec Json bằng Java, không bao giờ lo lỗi SnakeYAML parser nữa
        config.setCodec(new JsonJacksonCodec());

        return Redisson.create(config);
    }
}