package com.atguigu.gulimall.authserver.feign;

import com.atguigu.common.utils.R;
import com.atguigu.gulimall.authserver.po.WeiboAcctPo;
import com.atguigu.gulimall.authserver.vo.UserLoginVo;
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
    /**
     * 远程注册
     * @param vo
     * @return
     */
    @PostMapping("member/member/regist")
    public R regist(@RequestBody UserRegistVo vo);

    /**
     * 远程登录
     * @param vo
     * @return
     */
    @PostMapping("member/member/login")
    public R login(@RequestBody UserLoginVo vo);

    /**
     * 用户第三方账号登录
     * @param po
     * @return
     */
    @PostMapping("member/member/oauth2/login")
    public R oauthLogin(@RequestBody WeiboAcctPo po);
}
