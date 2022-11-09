package com.atguigu.gulimall.order.feign;

import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author zhuyuqi
 * @version v2.0
 * @interfaceName ProductFeignService
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/11/08 18:53
 */
@FeignClient("gulimall-product")
public interface ProductFeignService {
    @GetMapping("product/spuinfo/skuid/{id}")
    public R getSpuInfoBySkuId(@PathVariable("id") Long skuId);
}
