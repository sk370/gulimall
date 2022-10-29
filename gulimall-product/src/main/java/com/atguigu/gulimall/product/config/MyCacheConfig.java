package com.atguigu.gulimall.product.config;

import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * SpringCacahe的配置
 * @author zhuyuqi
 * @version v0.0.1
 * @className MyCacheConfig
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/10/29 17:14
 */
@EnableConfigurationProperties(CacheProperties.class)//开启指定类的绑定生效
@Configuration
@EnableCaching
public class MyCacheConfig {
    // 方式一：注入
//    @Autowired
//    CacheProperties cacheProperties;

    @Bean
    public RedisCacheConfiguration redisCacheConfiguration(CacheProperties cacheProperties){//方式二：传参
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();//默认配置
//        config = config.entryTtl();//修改ttl
        config = config.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()));//指定key的序列化规则——string，这里跟默认设置成了一样
        config = config.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericFastJsonRedisSerializer()));//指定value的序列化规则-json

        final CacheProperties.Redis redisProperties = cacheProperties.getRedis();
        if (redisProperties.getTimeToLive() != null) {
            config = config.entryTtl(redisProperties.getTimeToLive());
        }
        if (redisProperties.getKeyPrefix() != null) {
            config = config.prefixKeysWith(redisProperties.getKeyPrefix());
        }
        if (!redisProperties.isCacheNullValues()) {
            config = config.disableCachingNullValues();
        }
        if (!redisProperties.isUseKeyPrefix()) {
            config = config.disableKeyPrefix();
        }

        return config;
    }
}
