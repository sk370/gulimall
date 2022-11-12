package com.atguigu.gulimall.secondkill.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

/**
 * @author zhuyuqi
 * @version v0.0.1
 * @className ScheduleConfig
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/11/11 15:24
 */
@Configuration
@EnableScheduling//开启定时任务
@EnableAsync//开启定时任务的异步功能
public class ScheduleConfig {
}
