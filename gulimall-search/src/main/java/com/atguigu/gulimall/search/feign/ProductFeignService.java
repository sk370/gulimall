package com.atguigu.gulimall.search.feign;

import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author zhuyuqi
 * @version v2.0
 * @interfaceName ProductFeignService
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/10/31 21:48
 */
@FeignClient("gulimall-product")
public interface ProductFeignService {
//    @RequestMapping("/info/{attrId}")//转化为下面这种
//    public R info(@PathVariable("attrId") Long attrId){

    @GetMapping("product/attr/info/{attrId}")
    public R info(@PathVariable("attrId") Long attrId);
}
