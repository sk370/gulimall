package com.atguigu.gulimall.secondkill.service;

import com.atguigu.gulimall.secondkill.vo.to.SecKillSkuRedisTo;

import java.util.List;

/**
 * @author zhuyuqi
 * @version v0.0.1
 * @className SecKillService
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/11/11 15:30
 */
public interface SecKillService {
    void uploadSecKillSkuLatest3Days();

    List<SecKillSkuRedisTo> getCurrentSecKillSkus();

    SecKillSkuRedisTo getSkuSecKillInfo(Long skuId);

    String kill(String killId, String key, String num);
}
