package com.atguigu.gulimall.ware.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.atguigu.common.utils.R;

/**
 * @author zhuyuqi
 * @version v2.0
 * @interfaceName ProductFeignService
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/10/25 14:37
 */
@FeignClient("gulimall-product")
public interface ProductFeignService {
    /**
     *  product/skuinfo/info/{skuId}
     *  api/product/skuinfo/info/{skuId}
     *  1. 让所有请求经过网关：
     *      1.1 @FeignClient("gulimall-gateway")
     *      1.2 api/product/skuinfo/info/{skuId}
     *  2. 后台直接指定处理的服务
     *      2.1 @FeignClient("gulimall-product")
     *      2.2 product/skuinfo/info/{skuId}
     * @param skuId
     * @return
     */
    @RequestMapping("product/skuinfo/info/{skuId}")
    public R info(@PathVariable("skuId") Long skuId);
}
