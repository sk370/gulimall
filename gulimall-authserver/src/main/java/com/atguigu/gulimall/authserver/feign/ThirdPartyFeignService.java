package com.atguigu.gulimall.authserver.feign;

import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author zhuyuqi
 * @version v0.0.1
 * @className ThirdPartyFeignService
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/11/02 18:29
 */
@FeignClient("gulimall-third-party")
public interface ThirdPartyFeignService {
    @GetMapping("sms/sendcode")
    public R sendCode(@RequestParam("phone") String phone, @RequestParam("code") String code);
}
