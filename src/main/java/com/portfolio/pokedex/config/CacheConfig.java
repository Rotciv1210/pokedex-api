package com.portfolio.pokedex.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {

    @Value("${cache.ttl}")
    private long ttlSeconds;

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager manager = new CaffeineCacheManager("pokemon", "pokemon-list");
        manager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(ttlSeconds, TimeUnit.SECONDS)
                .maximumSize(500));
        return manager;
    }
}
