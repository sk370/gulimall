package com.atguigu.getway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/**
 * 网关相关配置：跨域
 * @author: INFINITY https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date: 2022/8/6 19:16
 */
@Configuration
public class GulimallCorsConfiguration {
    //网关是使用webflux进行响应式编程的，所以都选择web.relative包下的类

    /**
     * 跨域
     * @return
     */
    @Bean
    public CorsWebFilter corsWebFilter(){
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        // 1. 配置跨域
        corsConfiguration.addAllowedHeader("*");//允许所有请求头
        corsConfiguration.addAllowedMethod("*");//允许任意请求方法
        corsConfiguration.addAllowedOrigin("*");//允许所有请求来源
        corsConfiguration.setAllowCredentials(true);//允许携带cookie跨域

        source.registerCorsConfiguration("/**", corsConfiguration);// /**表示所有路径都需要跨域

        return new CorsWebFilter(source);
    }
}
