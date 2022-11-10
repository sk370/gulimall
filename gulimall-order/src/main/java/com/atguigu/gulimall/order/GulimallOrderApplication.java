package com.atguigu.gulimall.order;

import com.alibaba.cloud.seata.GlobalTransactionAutoConfiguration;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableDiscoveryClient
@SpringBootApplication(exclude = GlobalTransactionAutoConfiguration.class)//排除seata的全局事务自动配置，使用消息队列优化，提高高并发新能（不排除控制台会报错）
//@SpringBootApplication
@EnableRabbit//开启rabbitmq功能
@EnableFeignClients
@EnableAspectJAutoProxy(exposeProxy=true)//主启动类开启aspectj动态代理（不使用aspectj则为默认的jdk代理，需要接口才能代理）
@EnableTransactionManagement(proxyTargetClass = true)//主启动类开启代理对象事务控制（老师的没这句）
public class GulimallOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallOrderApplication.class, args);
    }

}
