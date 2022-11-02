package com.atguigu.gulimall.product.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 商品详情页信息异步加载的线程池
 * @author zhuyuqi
 * @version v0.0.1
 * @className MyThreadConfig
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/11/02 09:15
 */
//@EnableConfigurationProperties(ThreadPoolConfigProperties.class)//如果ThreadPoolConfigProperties没有使用@Component将自己加入到容器中，则需要这句开启注解配置功能
@Configuration
public class MyThreadConfig {
    @Bean
    public ThreadPoolExecutor threadPoolExecutor(ThreadPoolConfigProperties pool){
        return new ThreadPoolExecutor(
                pool.getCoreSize(),
                pool.getMaxSize(),
                pool.getKeepAliveTime(),
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(10000),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy()
        );
    }
}
