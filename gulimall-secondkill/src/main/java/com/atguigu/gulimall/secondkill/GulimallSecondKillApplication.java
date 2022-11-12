package com.atguigu.gulimall.secondkill;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author zhuyuqi
 * @version v0.0.1
 * @className GulimallSecondKillApplication
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/11/11 14:35
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableDiscoveryClient
@EnableFeignClients
public class GulimallSecondKillApplication {
    public static void main(String[] args) {
        SpringApplication.run(GulimallSecondKillApplication.class,args);
    }
}
