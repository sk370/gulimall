package com.atguigu.gulimall.coupon.service;

import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.coupon.entity.SeckillSessionEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * 秒杀活动场次
 *
 * @author zhuyuqi
 * @email icerivericeriver@hotmail.com
 * @date 2022-07-30 10:40:48
 */
public interface SeckillSessionService extends IService<SeckillSessionEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

