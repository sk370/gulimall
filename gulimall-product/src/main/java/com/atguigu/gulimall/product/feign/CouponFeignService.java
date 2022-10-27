package com.atguigu.gulimall.product.feign;

import com.atguigu.common.to.SkuReductionTo;
import com.atguigu.common.to.SpuBoundTo;
import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 调用gulimall-coupon的远程方法（优惠）
 * @author zhuyuqi
 * @version v0.0.1
 * @className SpuFeignService
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/10/24 15:55
 */
@FeignClient("gulimall-coupon")//声明调用的远程服务
public interface CouponFeignService {
    @PostMapping("coupon/spubounds/save")
    R saveSpuBounds(@RequestBody SpuBoundTo spuBoundTo);//由于openfeign传参时，SpuBoundTo转换为了json对象，gulimall-coupon接收到后转换为了SpuBoundsEntity对象，只要二者的属性一致，就可以当作相同。而不是必须要求是同一类型的对象。

    @PostMapping("coupon/skufullreduction/saveinfo")
    R saveSkuReduction(@RequestBody SkuReductionTo skuReductionTo);
}
