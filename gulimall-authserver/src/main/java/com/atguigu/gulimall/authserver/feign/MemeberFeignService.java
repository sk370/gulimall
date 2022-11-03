package com.atguigu.gulimall.authserver.feign;

import com.atguigu.common.utils.R;
import com.atguigu.gulimall.authserver.vo.UserRegistVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author zhuyuqi
 * @version v2.0
 * @interfaceName MemeberFeignService
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/11/02 22:15
 */
@FeignClient("gulimall-member")
public interface MemeberFeignService {

    @PostMapping("member/member/regist")
    public R regist(@RequestBody UserRegistVo vo);
}
