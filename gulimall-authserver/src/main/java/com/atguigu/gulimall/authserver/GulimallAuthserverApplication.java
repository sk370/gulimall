package com.atguigu.gulimall.authserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableRedisHttpSession
@EnableDiscoveryClient
@EnableFeignClients//能够调用远程服务
@SpringBootApplication
public class GulimallAuthserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(GulimallAuthserverApplication.class, args);
	}

}