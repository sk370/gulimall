package com.atguigu.gulimall.product.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.atguigu.common.to.es.SkuESModel;
import com.atguigu.common.utils.R;

/**
 * ES有关的接口
 * @author zhuyuqi
 * @version v2.0
 * @interfaceName SearchFeignService
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/10/27 10:37
 */
@FeignClient("gulimall-search")
public interface SearchFeignService {
    @PostMapping("search/save/product")
    public R productStatusUp(@RequestBody List<SkuESModel> skuESModels);
}
