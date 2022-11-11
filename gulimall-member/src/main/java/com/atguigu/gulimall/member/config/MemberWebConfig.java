package com.atguigu.gulimall.member.config;

import com.atguigu.gulimall.member.inteceptor.LoginUserInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author zhuyuqi
 * @version v0.0.1
 * @className MemberWebConfig
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/11/07 21:29
 */
@Configuration
public class MemberWebConfig implements WebMvcConfigurer {
    @Autowired
    LoginUserInterceptor loginUserInterceptor;

    /**
     * 使得拦截器生效
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginUserInterceptor).addPathPatterns("/**");//拦截所有请求，进行登录验证
    }
}
