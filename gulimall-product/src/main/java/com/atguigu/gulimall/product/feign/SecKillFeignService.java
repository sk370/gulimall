package com.atguigu.gulimall.product.feign;

import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author zhuyuqi
 * @version v2.0
 * @interfaceName SecKillFeignService
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/11/11 22:09
 */
@FeignClient("gulimall-secondkill")
public interface SecKillFeignService {
    @GetMapping("sku/seckill/{skuId}")
    public R getSkuSecKillInfo(@PathVariable("skuId") Long skuId);
}
