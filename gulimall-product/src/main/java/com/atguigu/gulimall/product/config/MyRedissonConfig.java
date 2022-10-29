package com.atguigu.gulimall.product.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * redis分布式框架——锁是其功能之一
 * @author zhuyuqi
 * @version v0.0.1
 * @className MyRedissonConfig
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/10/29 10:15
 */
@Configuration
public class MyRedissonConfig {
    @Bean(destroyMethod="shutdown")
    public RedissonClient redisson() throws IOException {
        Config config = new Config();
//        config.useClusterServers().addNodeAddress("127.0.0.1:7004", "127.0.0.1:7001");//集群模式
        config.useSingleServer().setAddress("redis://192.168.56.10:6379");//设置单机模式
        return Redisson.create(config);//
    }
}
