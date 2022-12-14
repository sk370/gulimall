package com.atguigu.gulimall.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

//@EnableCaching//配置类中进行自定义配置写了，这里就不用写了
@EnableRedisHttpSession//开启spring session功能
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.atguigu.gulimall.product.feign")//开启远程调用功能，并指明远程接口所在包（也可以不写路径，不写的前提时父子包），当专门写feign的配置类时，必须写扫描路径
//@MapperScan("com.atguigu.gulimall.product.dao")//config包下的mybatis配置类写了，这里就不用写了
@SpringBootApplication
public class GulimallProductApplication {
    public static void main(String[] args) {
        SpringApplication.run(GulimallProductApplication.class, args);
    }
}
