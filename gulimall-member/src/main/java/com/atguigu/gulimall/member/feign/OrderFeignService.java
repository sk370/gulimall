package com.atguigu.gulimall.member.feign;

import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * @author zhuyuqi
 * @version v2.0
 * @interfaceName OrderFeignService
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/11/10 22:23
 */
@FeignClient("gulimall-order")
public interface OrderFeignService {
    @PostMapping("order/order/listWithItem")
    public R listWithItem(@RequestBody Map<String, Object> params);
}
