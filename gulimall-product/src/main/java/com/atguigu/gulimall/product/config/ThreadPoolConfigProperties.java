package com.atguigu.gulimall.product.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * 线程池配置文件
 * @author zhuyuqi
 * @version v0.0.1
 * @className ThreadPoolConfigProperties
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/11/02 09:18
 */
@ConfigurationProperties(prefix = "gulimall.thread")
@Component
@Data
public class ThreadPoolConfigProperties {
    private Integer coreSize;
    private Integer maxSize;
    private Integer keepAliveTime;

}
