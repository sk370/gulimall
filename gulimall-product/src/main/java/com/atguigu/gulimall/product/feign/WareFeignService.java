package com.atguigu.gulimall.product.feign;

import com.atguigu.common.to.SkuHasStockVo;
import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 调用gulimall-ware接口（库存）
 * @author zhuyuqi
 * @version v2.0
 * @interfaceName WareFeignService
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/10/27 09:09
 */
@FeignClient("gulimall-ware")//声明调用的远程服务
public interface WareFeignService {

    @PostMapping("ware/waresku/hasStock")
    R getSkuHasStock(@RequestBody List<Long> skuIds);
}
