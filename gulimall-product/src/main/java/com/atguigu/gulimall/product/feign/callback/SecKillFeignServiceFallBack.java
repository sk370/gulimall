package com.atguigu.gulimall.product.feign.callback;

import com.atguigu.common.exception.BizCodeEnum;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.product.feign.SecKillFeignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 调用秒杀服务失败的回调
 * @author zhuyuqi
 * @version v0.0.1
 * @className SecKillFeignServiceFallBack
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/11/13 11:49
 */
@Component
@Slf4j
public class SecKillFeignServiceFallBack implements SecKillFeignService {
    @Override
    public R getSkuSecKillInfo(Long skuId) {
        log.error("熔断方法调用……SecKillFeignServiceFallBack");
        return R.error(BizCodeEnum.TOO_MANY_REQUEST.getCode(), BizCodeEnum.TOO_MANY_REQUEST.getMsg());
    }
}
