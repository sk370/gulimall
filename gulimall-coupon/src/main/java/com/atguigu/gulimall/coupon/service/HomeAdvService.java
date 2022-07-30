package com.atguigu.gulimall.coupon.service;

import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.coupon.entity.HomeAdvEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * 首页轮播广告
 *
 * @author zhuyuqi
 * @email icerivericeriver@hotmail.com
 * @date 2022-07-30 10:40:48
 */
public interface HomeAdvService extends IService<HomeAdvEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

