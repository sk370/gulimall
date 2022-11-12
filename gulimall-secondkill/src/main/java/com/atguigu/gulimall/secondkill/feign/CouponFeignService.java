package com.atguigu.gulimall.secondkill.feign;

import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author zhuyuqi
 * @version v0.0.1
 * @className CouponFeignService
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/11/11 15:33
 */
@FeignClient("gulimall-coupon")
public interface CouponFeignService {
    @GetMapping("coupon/seckillsession/latest3DaysSession")
    public R getLatest3DaysSession();
}
