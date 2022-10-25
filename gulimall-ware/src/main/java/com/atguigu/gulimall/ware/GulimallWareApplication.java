package com.atguigu.gulimall.ware;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;

//@EnableDiscoveryClient//在config。MyBatisConfig中配置了
@SpringBootApplication
@EnableFeignClients
@EnableTransactionManagement//开启事务
//@MapperScan("com.atguigu.gulimall.ware.dao")//在config。MyBatisConfig中配置了
public class GulimallWareApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallWareApplication.class, args);
    }

}
