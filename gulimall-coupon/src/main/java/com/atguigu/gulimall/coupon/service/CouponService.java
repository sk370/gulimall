package com.atguigu.gulimall.coupon.service;

import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * 优惠券信息
 *
 * @author zhuyuqi
 * @email icerivericeriver@hotmail.com
 * @date 2022-07-30 10:40:49
 */
public interface CouponService extends IService<CouponEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

