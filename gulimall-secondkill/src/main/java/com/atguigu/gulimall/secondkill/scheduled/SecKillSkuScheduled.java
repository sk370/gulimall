package com.atguigu.gulimall.secondkill.scheduled;

import com.atguigu.gulimall.secondkill.service.SecKillService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 秒杀商品定时上架：假设为每晚3点，上架最近三天的秒杀商品
 *                  当天00:00:00 - 23:59:59
 * @author zhuyuqi
 * @version v0.0.1
 * @className SecKillSkuScheduled
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/11/11 14:58
 */
@Service
@Slf4j
public class SecKillSkuScheduled {
    @Autowired
    SecKillService secKillService;
    @Autowired
    RedissonClient redissonClient;

    private final String upload_lock = "seckill:upload:lock";

    @Async//异步执行
    @Scheduled(cron = "0 * * * * ?")//开启一个定时任务（每3秒执行一次）
    public void uploadSecKillSkuLatest3Days(){
        log.info("上架秒杀商品信息");
        RLock lock = redissonClient.getLock(upload_lock);//分布式锁实现幂等性
        lock.lock(10, TimeUnit.SECONDS);
        try {
            secKillService.uploadSecKillSkuLatest3Days();
        }finally {
            lock.unlock();
        }
    }
}
