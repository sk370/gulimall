package com.atguigu.gulimall.authserver.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 没有方法内容，只是跳转页面的请求
 * @author zhuyuqi
 * @version v0.0.1
 * @className GulimallWebConfig
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/11/02 14:08
 */
@Configuration
public class GulimallWebConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        /**
         *     @GetMapping({"/","/login.html"})
         *     public String loginPage(){
         *         return "login";
         *     }
         *
         *     @GetMapping("/register.html")
         *     public String regPage(){
         *         return "register";
         *     }
         */
        registry.addViewController("/login.html").setViewName("login");
        registry.addViewController("/register.html").setViewName("register");

    }
}
