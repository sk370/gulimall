package com.atguigu.gulimall.ware.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;

/**
 * @author zhuyuqi
 * @version v0.0.1
 * @className MyBatisConfig
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/10/25 12:58
 */
@Configuration
@MapperScan("com.atguigu.gulimall.ware.dao")
@EnableDiscoveryClient
public class MyBatisConfig {

    /**
     * 引入分页插件
     * @return
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        paginationInterceptor.setOverflow(true);//设置请求的页面大于最大页后的操作，true回到首页，false继续请求，默认false
        paginationInterceptor.setLimit(1000);//设置最大但也限制数量，默认500条，-1表示不受限制
        return  paginationInterceptor;
    }
}
