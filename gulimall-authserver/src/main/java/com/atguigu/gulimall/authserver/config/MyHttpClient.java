package com.atguigu.gulimall.authserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * 第三方登录请求客户端
 * @author zhuyuqi
 * @version v0.0.1
 * @className MyHttpClient
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/11/03 19:04
 */
@Configuration
public class MyHttpClient {
    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
