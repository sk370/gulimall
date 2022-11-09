package com.atguigu.gulimall.order.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 配置feign的相关信息
 * @author zhuyuqi
 * @version v0.0.1
 * @className FeignConfig
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/11/08 09:19
 */
@Configuration
public class FeignConfig {
    /**
     * 请求拦截器，获得旧请求的cookie信息
     * @return
     */
    @Bean("requestInterceptor")
    public RequestInterceptor requestInterceptor(){
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();//即threadlocal
                HttpServletRequest request = attributes.getRequest();//旧请求
                if(request != null){
                    String cookie = request.getHeader("Cookie");
                    template.header("Cookie", cookie);//给新请求同步cookie
                }
            }
        };
    }
}
