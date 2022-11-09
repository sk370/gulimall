package com.atguigu.gulimall.order;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableDiscoveryClient
@SpringBootApplication
@EnableRabbit//开启rabbitmq功能
@EnableFeignClients
@EnableAspectJAutoProxy(exposeProxy=true)//主启动类开启aspectj动态代理（不使用aspectj则为默认的jdk代理，需要接口才能代理）
@EnableTransactionManagement(proxyTargetClass = true)//主启动类开启代理对象事务控制
public class GulimallOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallOrderApplication.class, args);
    }

}
