package com.atguigu.gulimall.order.feign;

import com.atguigu.gulimall.order.vo.MemberAddressVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author zhuyuqi
 * @version v0.0.1
 * @className MemberFeignService
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/11/07 22:08
 */
@FeignClient("gulimall-member")
public interface MemberFeignService {
    @GetMapping("member/memberreceiveaddress/{memberId}/address")
    public List<MemberAddressVo> getAddress(@PathVariable("memberId") Long memberId);
}
