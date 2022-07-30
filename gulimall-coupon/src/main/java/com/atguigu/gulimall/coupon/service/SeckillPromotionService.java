package com.atguigu.gulimall.coupon.service;

import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.coupon.entity.SeckillPromotionEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * 秒杀活动
 *
 * @author zhuyuqi
 * @email icerivericeriver@hotmail.com
 * @date 2022-07-30 10:40:48
 */
public interface SeckillPromotionService extends IService<SeckillPromotionEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

