package com.atguigu.gulimall.ware;

import com.alibaba.cloud.seata.GlobalTransactionAutoConfiguration;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;

//@EnableDiscoveryClient//在config。MyBatisConfig中配置了
@SpringBootApplication(exclude = GlobalTransactionAutoConfiguration.class)//排除seata的全局事务自动配置，使用消息队列优化，提高高并发新能（不排除控制台会报错）
//@SpringBootApplication
@EnableFeignClients
@EnableTransactionManagement//开启事务
@EnableRabbit//开启rrabbitmq功能
//@MapperScan("com.atguigu.gulimall.ware.dao")//在config。MyBatisConfig中配置了
public class GulimallWareApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallWareApplication.class, args);
    }

}
