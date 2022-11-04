package com.atguigu.gulimall.search.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

/**
 * @author zhuyuqi
 * @version v0.0.1
 * @className GulimallSessionConfig
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/11/04 20:00
 */
@Configuration
public class GulimallSessionConfig {
    /**
     *  设置作用域名及session名字
     * @return
     */
    @Bean
    public CookieSerializer cookieSerializer(){
        DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
        cookieSerializer.setDomainName("gulimall.com");
        cookieSerializer.setCookieName("GULISESSION");
        return  cookieSerializer;
    }

    /**
     * 使用json序列化
     * @return
     */
    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer(){
        return new GenericJackson2JsonRedisSerializer();
    }
}
